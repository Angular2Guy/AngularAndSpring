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

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.JwtUtils;
import ch.xxx.trader.domain.common.PasswordEncryption;
import ch.xxx.trader.domain.exceptions.AuthenticationException;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import reactor.core.publisher.Mono;

@Profile("!kafka & !prod-kafka")
@Service
public class MyUserServiceDb extends MyUserServiceBean implements MyUserService {
	private Logger LOGGER = LoggerFactory.getLogger(MyUserServiceDb.class);
	
	public MyUserServiceDb(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository,
			MyMessageProducer myMessageProducer) {
		super(jwtTokenProvider, passwordEncoder, passwordEncryption, myMongoRepository);
	}

	@Override
	public Mono<Boolean> postLogout(String bearerStr) {
		String username = this.jwtTokenProvider.getUsername(JwtUtils.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
		String uuid = this.jwtTokenProvider.getUuid(JwtUtils.resolveToken(bearerStr)
				.orElseThrow(() -> new AuthenticationException("Invalid bearer string.")));
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

}
