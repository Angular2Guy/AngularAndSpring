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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import ch.xxx.trader.domain.model.entity.MyMongoRepository;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class MyUserServiceBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyUserServiceBean.class);
	private static final long LOGOUT_TIMEOUT = 185L;
	protected final JwtTokenService jwtTokenService;
	private final PasswordEncryption passwordEncryption;
	protected final MyMongoRepository myMongoRepository;
	private final PasswordEncoder passwordEncoder;

	public MyUserServiceBean(JwtTokenService jwtTokenProvider, PasswordEncoder passwordEncoder,
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository) {
		this.jwtTokenService = jwtTokenProvider;
		this.passwordEncryption = passwordEncryption;
		this.myMongoRepository = myMongoRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void updateLoggedOutUsers() {
		List<RevokedToken> revokedTokens = this.myMongoRepository.find(new Query(), RevokedToken.class).stream()
				.filter(myRevokedToken -> myRevokedToken.getLastLogout() == null || !myRevokedToken.getLastLogout()
						.isBefore(LocalDateTime.now().minusSeconds(LOGOUT_TIMEOUT)))
				.toList();
		this.jwtTokenService.updateLoggedOutUsers(revokedTokens);
		revokedTokens.stream()
				.filter(myRevokedToken -> myRevokedToken.getLastLogout() != null
						&& myRevokedToken.getLastLogout().isBefore(LocalDateTime.now().minusSeconds(LOGOUT_TIMEOUT)))
				.forEach(myRevokedToken -> this.myMongoRepository.remove(myRevokedToken));
	}

	public AuthCheck postAuthorize(AuthCheck authcheck, Map<String, String> header) {
		Optional<String> token = WebUtils.extractToken(header);
		Query query = new Query();
		query.addCriteria(Criteria.where("salt").is(authcheck.getHash()));
		MyUser user = this.myMongoRepository.findOne(query, MyUser.class).orElseGet(MyUser::new);
		return mapMyUser(user, authcheck, token);
	}

	private AuthCheck mapMyUser(MyUser myUser, AuthCheck authcheck, Optional<String> token) {
		Optional<Jws<Claims>> claims = this.jwtTokenService.getClaims(token);
		if (myUser.getUserId() != null && claims.isPresent()
				&& myUser.getUserId().equals(claims.get().getBody().getSubject())
				&& new Date().before(claims.get().getBody().getExpiration())) {
			return new AuthCheck(authcheck.getHash(), authcheck.getPath(), true);
		}
		return new AuthCheck(authcheck.getHash(), authcheck.getPath(), false);
	}

	public MyUser postUserSignin(MyUser myUser, boolean persist, boolean check) {
		Query query = new Query(Criteria.where("userId").is(myUser.getUserId()));
		return check
				? signinHelp(this.myMongoRepository.findOne(query, MyUser.class).orElse(myUser), persist)
				: this.saveSignin(myUser);
	}

	private MyUser signinHelp(MyUser myUser1, boolean persist) {
		if (myUser1.get_id() == null) {
			String salt;
			try {
				salt = this.passwordEncryption.generateSalt();
			} catch (Exception e) {
				throw new AuthenticationException("Generating salt failed.", e);
			}
			String encryptedPassword = this.passwordEncoder.encode(myUser1.getPassword());
			myUser1.setPassword(encryptedPassword);
			myUser1.setSalt(salt);
			return persist ? saveSignin(myUser1) : myUser1;
		}
		return new MyUser();
	}

	private MyUser saveSignin(MyUser myUser1) {
		MyUser myUser2 = this.myMongoRepository.save(myUser1);
		myUser2.setPassword("XXX");
		myUser2.setSalt("YYY");
		return myUser2;
	}

	public Boolean postLogout(String bearerStr) {
		String username = getTokenUsername(bearerStr);
		String uuid = getTokenUuid(bearerStr);
		Query query = new Query(Criteria.where("uuid").is(uuid));
		List<RevokedToken> revokedTokens = this.myMongoRepository.find(query, RevokedToken.class).stream()
				.filter(myRevokedToken -> myRevokedToken.getUuid().equals(uuid)).toList();
		if (!revokedTokens.isEmpty()) {
			LOGGER.warn("Duplicate logout for user {}", username);
			return Boolean.TRUE;
		}
		this.myMongoRepository.insert(new RevokedToken(null, username, uuid, LocalDateTime.now()));
		return Boolean.TRUE;
	}

	protected String getTokenUuid(String bearerStr) {
		return this.jwtTokenService.getUuid(JwtUtils.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
	}

	protected String getTokenUsername(String bearerStr) {
		return this.jwtTokenService.getUsername(JwtUtils.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
	}

	public MyUser postUserLogin(MyUser myUser) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(myUser.getUserId()));
		MyUser user1 = this.myMongoRepository.findOne(query, MyUser.class).orElseGet(MyUser::new);
		this.delayElement();
		return loginHelp(user1, myUser.getPassword());
	}

	private void delayElement() {
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private MyUser loginHelp(MyUser user, String passwd) {
		if (user.getUserId() != null) {
			if (this.passwordEncoder.matches(passwd, user.getPassword())) {
				String jwtToken = this.jwtTokenService.createToken(user.getUserId(), Arrays.asList(Role.USERS));
				user.setToken(jwtToken);
				user.setPassword("XXX");
				return user;
			}
		}
		return new MyUser();
	}

	public RefreshTokenDto refreshToken(String bearerStr) {
		Optional<String> tokenOpt = this.jwtTokenService.resolveToken(bearerStr);
		if (tokenOpt.isEmpty()) {
			throw new AuthenticationException("Invalid token");
		}
		String newToken = this.jwtTokenService.refreshToken(tokenOpt.get());
		LOGGER.info("Jwt Token refreshed.");
		return new RefreshTokenDto(newToken);
	}
}