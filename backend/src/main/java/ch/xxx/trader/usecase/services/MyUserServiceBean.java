/**
 *    Copyright 2016 Sven Loesekann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.trader.usecase.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.xxx.trader.domain.common.JwtUtils;
import ch.xxx.trader.domain.common.PasswordEncryption;
import ch.xxx.trader.domain.common.Role;
import ch.xxx.trader.domain.common.WebUtils;
import ch.xxx.trader.domain.exceptions.AuthenticationException;
import ch.xxx.trader.domain.model.dto.AuthCheck;
import ch.xxx.trader.domain.model.dto.RefreshTokenDto;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public class MyUserServiceBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyUserServiceBean.class);
	private static final long LOGOUT_TIMEOUT = 185L;
	protected final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncryption passwordEncryption;
	protected final MyMongoRepository myMongoRepository;
	private final PasswordEncoder passwordEncoder;
	 
	private Disposable updateLoggedOutUsersDisposable = Mono.empty().subscribe();

	public MyUserServiceBean(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncryption = passwordEncryption;
		this.myMongoRepository = myMongoRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void updateLoggedOutUsers() {
		this.updateLoggedOutUsersDisposable.dispose();
		this.updateLoggedOutUsersDisposable = this.myMongoRepository.find(new Query(), RevokedToken.class).collectList()
				.flatMapIterable(revokedTokens -> {
					this.jwtTokenProvider.updateLoggedOutUsers(revokedTokens.stream()
//							.filter(StreamHelpers.distinctByKey(value -> value.getUuid()))
							.filter(myRevokedToken -> myRevokedToken.getLastLogout() == null || !myRevokedToken
									.getLastLogout().isBefore(LocalDateTime.now().minusSeconds(LOGOUT_TIMEOUT)))
							.toList());
					return revokedTokens;
				})
				.filter(myRevokedToken -> myRevokedToken.getLastLogout() != null
						&& myRevokedToken.getLastLogout().isBefore(LocalDateTime.now().minusSeconds(LOGOUT_TIMEOUT)))
				.flatMap(revokeToken -> this.myMongoRepository.remove(Mono.just(revokeToken))).subscribe();
	}

	public Mono<AuthCheck> postAuthorize(AuthCheck authcheck, Map<String, String> header) {
		Optional<String> token = WebUtils.extractToken(header);
		Query query = new Query();
		query.addCriteria(Criteria.where("salt").is(authcheck.getHash()));
		return this.myMongoRepository.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser()))
				.map(user -> mapMyUser(user, authcheck, token));
	}

	private AuthCheck mapMyUser(MyUser myUser, AuthCheck authcheck, Optional<String> token) {
		Optional<Jws<Claims>> claims = this.jwtTokenProvider.getClaims(token);
		if (myUser.getUserId() != null && claims.isPresent()
				&& myUser.getUserId().equals(claims.get().getBody().getSubject())
				&& new Date().before(claims.get().getBody().getExpiration())) {
			return new AuthCheck(authcheck.getHash(), authcheck.getPath(), true);
		}
		return new AuthCheck(authcheck.getHash(), authcheck.getPath(), false);
	}

	public Mono<MyUser> postUserSignin(MyUser myUser, boolean persist, boolean check) {
		Query query = new Query(Criteria.where("userId").is(myUser.getUserId()));
		return check ? this.myMongoRepository.findOne(query, MyUser.class).switchIfEmpty(Mono.just(myUser))
				.flatMap(myUser1 -> signinHelp(myUser1, persist)) : this.saveSignin(myUser);
	}

	private Mono<MyUser> signinHelp(MyUser myUser1, boolean persist) {
		if (myUser1.get_id() == null) {
			String salt;
			try {
				salt = this.passwordEncryption.generateSalt();
			} catch (NoSuchAlgorithmException e) {
				throw new AuthenticationException("Generating salt failed.", e);
			}
			String encryptedPassword = this.passwordEncoder.encode(myUser1.getPassword());
			myUser1.setPassword(encryptedPassword);
			myUser1.setSalt(salt);
			return persist ? saveSignin(myUser1) : Mono.just(myUser1);
		}
		return Mono.just(new MyUser());
	}

	private Mono<MyUser> saveSignin(MyUser myUser1) {
		return this.myMongoRepository.save(myUser1).flatMap(myUser2 -> {
			myUser2.setPassword("XXX");
			myUser2.setSalt("YYY");
			return Mono.just(myUser2);
		});
	}

	public Mono<Boolean> postLogout(String bearerStr) {
		String username = getTokenUsername(bearerStr);
		String uuid = getTokenUuid(bearerStr);
		Query query = new Query(Criteria.where("uuid").is(uuid));
		return this.myMongoRepository.find(query, RevokedToken.class)
				.filter(myRevokedToken -> myRevokedToken.getUuid().equals(uuid)).collectList()
				.doOnEach(myRevokedTokens -> {
					if (myRevokedTokens.hasValue() && !myRevokedTokens.get().isEmpty())
						LOGGER.warn("Duplicate logout for user {}", username);
				})
				.flatMap(myRevokedTokens -> !myRevokedTokens.isEmpty() ? Mono.just(Boolean.TRUE)
						: this.myMongoRepository
								.insert(Mono.just(new RevokedToken(null, username, uuid, LocalDateTime.now())))
								.then(Mono.just(Boolean.TRUE)));
	}

	protected String getTokenUuid(String bearerStr) {
		return this.jwtTokenProvider.getUuid(JwtUtils.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
	}

	protected String getTokenUsername(String bearerStr) {
		return this.jwtTokenProvider.getUsername(JwtUtils.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
	}

	
	public Mono<MyUser> postUserLogin(MyUser myUser) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(myUser.getUserId()));
		return this.myMongoRepository.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser()))
				.delayElement(Duration.ofSeconds(3L))
				.map(user1 -> loginHelp(user1, myUser.getPassword()));
	}

	private MyUser loginHelp(MyUser user, String passwd) {
		if (user.getUserId() != null) {
			if (this.passwordEncoder.matches(passwd, user.getPassword())) {
				String jwtToken = this.jwtTokenProvider.createToken(user.getUserId(), Arrays.asList(Role.USERS));
				user.setToken(jwtToken);
				user.setPassword("XXX");
				return user;
			}
		}
		return new MyUser();
	}

	public RefreshTokenDto refreshToken(String bearerStr) {
		Optional<String> tokenOpt = this.jwtTokenProvider.resolveToken(bearerStr);
		if (tokenOpt.isEmpty()) {
			throw new AuthenticationException("Invalid token");
		}
		String newToken = this.jwtTokenProvider.refreshToken(tokenOpt.get());
		LOGGER.info("Jwt Token refreshed.");
		return new RefreshTokenDto(newToken);
	}
}
