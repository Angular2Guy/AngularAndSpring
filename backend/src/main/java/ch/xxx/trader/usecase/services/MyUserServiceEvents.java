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
import java.util.ArrayList;
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
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Profile("kafka | prod")
@Service
public class MyUserServiceEvents extends MyUserServiceBean implements MyUserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyUserServiceEvents.class);
//	private static final long LOGOUT_TIMEOUT = 95L;
	private final MyEventProducer myMessageProducer;
	private final Sinks.Many<MyUser> myUserSink = Sinks.many().multicast().onBackpressureBuffer();
	private final ConnectableFlux<MyUser> myUserFlux = this.myUserSink.asFlux().publish();

	public MyUserServiceEvents(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository,
			MyEventProducer myMessageProducer) {
		super(jwtTokenProvider, passwordEncoder, passwordEncryption, myMongoRepository);
		this.myMessageProducer = myMessageProducer;
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
		Mono<MyUser> MyUserResult = this.myUserFlux.autoConnect()
				.filter(myUser1 -> myUser.getUserId().equalsIgnoreCase(myUser1.getUserId())).shareNext();
		return super.postUserSignin(myUser, false, true).flatMap(dto -> this.myMessageProducer.sendNewUser(dto))
				.zipWith(MyUserResult, (myUser1, msgMyUser1) -> msgMyUser1).flatMap(myUser1 -> {
					// LOGGER.info("MyUser signin result: {}",myUser1);
					return Mono.just(myUser1);
				});
	}

	public Mono<MyUser> userSigninMsg(MyUser myUser) {
		return super.postUserSignin(myUser, true, false).flatMap(myUser1 -> {
			if (this.myUserSink.tryEmitNext(myUser1).isFailure()) {
				LOGGER.info("Emit to myUserSink failed. {}", myUser1);
			}
			return Mono.just(myUser1);
		});
	}

	@Override
	public Mono<Boolean> postLogout(String token) {
		String username = this.getTokenUsername(token);
		String uuid = this.getTokenUuid(token);
		List<RevokedToken> revokedTokens = new ArrayList<>();
		revokedTokens.add(new RevokedToken(null, username, uuid, LocalDateTime.now()));
		return revokedTokens.stream()
				.map(myRevokedToken -> this.myMessageProducer.sendUserLogout(myRevokedToken)
						.flatMap(value -> Mono.just(value != null)))
				.reduce((result1, result2) -> Mono.just(result1.block() == true && result2.block() == true))
				.orElse(Mono.just(Boolean.FALSE));
	}

	public Mono<Boolean> logoutMsg(RevokedTokensDto revokedTokensDto) {
		return Mono.just(this.updateLoggedOutUsers(revokedTokensDto.getRevokedTokens()));
	}
}
