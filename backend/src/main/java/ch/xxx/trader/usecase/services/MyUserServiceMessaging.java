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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.PasswordEncryption;
import ch.xxx.trader.domain.model.dto.RevokedTokensDto;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import ch.xxx.trader.usecase.mappers.MessageMapper;
import reactor.core.publisher.Mono;

@Profile("kafka | prod-kafka")
@Service
public class MyUserServiceMessaging extends MyUserServiceBean implements MyUserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyUserServiceMessaging.class);
	private static final long LOGOUT_TIMEOUT = 95L;
	private final MyMessageProducer myMessageProducer;
	private final MessageMapper messageMapper;

	public MyUserServiceMessaging(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository,
			MyMessageProducer myMessageProducer, MessageMapper messageMapper) {
		super(jwtTokenProvider, passwordEncoder, passwordEncryption, myMongoRepository);
		this.myMessageProducer = myMessageProducer;
		this.messageMapper = messageMapper;
	}

	@Override
	public void updateLoggedOutUsers() {
		// do nothing
	}

	public Boolean updateLoggedOutUsers(List<RevokedToken> revokedTokens) {
		this.jwtTokenProvider.updateLoggedOutUsers(revokedTokens);
		return Boolean.TRUE;
	}

	@Override
	public Mono<MyUser> postUserSignin(MyUser myUser) {
		return super.postUserSignin(myUser, false, true).flatMap(dto -> this.myMessageProducer.sendNewUser(dto));
	}

	public Mono<Boolean> userSigninMsg(MyUser myUser) {
		return super.postUserSignin(myUser, true, false).flatMap(dto -> Mono.just(dto.get_id() != null));
	}

	@Override
	public Mono<Boolean> postLogout(String token) {
		String username = this.getTokenUsername(token);
		String uuid = this.getTokenUuid(token);
		return this.myMessageProducer.sendUserLogout(new RevokedToken(null, username, uuid, LocalDateTime.now()))
				.flatMap(value -> Mono.just(value != null));
	}

	public Mono<Boolean> logoutMsg(RevokedTokensDto revokedTokensDto) {
		return Mono.just(this.updateLoggedOutUsers(revokedTokensDto.getRevokedTokens()));
	}
}
