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
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.PasswordEncryption;
import ch.xxx.trader.domain.common.Role;
import ch.xxx.trader.domain.common.WebUtils;
import ch.xxx.trader.domain.model.AuthCheck;
import ch.xxx.trader.domain.model.MyUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import reactor.core.publisher.Mono;

@Service
public class MyUserService {
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncryption passwordEncryption; 
	private final MyMongoRepository myMongoRepository;
	private final PasswordEncoder passwordEncoder; 
	
	public MyUserService(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, 
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncryption = passwordEncryption;
		this.myMongoRepository = myMongoRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Mono<AuthCheck> postAuthorize(AuthCheck authcheck, Map<String,String> header) {				
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

	public Mono<MyUser> postUserSignin(MyUser myUser)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(myUser.getUserId()));
		MyUser user = this.myMongoRepository.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser())).block();
		if (user.getUserId() == null) {
			String salt = this.passwordEncryption.generateSalt();
			String encryptedPassword = this.passwordEncoder.encode(myUser.getPassword());
			myUser.setPassword(encryptedPassword);
			myUser.setSalt(salt);
			this.myMongoRepository.save(myUser).block();
			return Mono.just(myUser);
		}
		return Mono.just(new MyUser());
	}

	public Mono<MyUser> postLogout(String hash) {
		Query query = new Query();
		query.addCriteria(Criteria.where("salt").is(hash));
		return this.myMongoRepository.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser()))
				.map(user1 -> loginHelp(user1, ""));
	}

	public Mono<MyUser> postUserLogin(MyUser myUser)
			throws NoSuchAlgorithmException, InvalidKeySpecException {		
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(myUser.getUserId()));
		return this.myMongoRepository.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser()))
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
}
