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
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.PasswordEncryption;
import ch.xxx.trader.domain.model.dto.RevokedTokensDto;
import ch.xxx.trader.domain.model.entity.MyMongoRepository;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import ch.xxx.trader.domain.services.MyEventProducer;
import ch.xxx.trader.domain.services.MyUserService;

@Profile("kafka | prod")
@Service
public class MyUserServiceEvents extends MyUserServiceBean implements MyUserService {
	private final MyEventProducer myEventProducer;

	public MyUserServiceEvents(JwtTokenService jwtTokenProvider, PasswordEncoder passwordEncoder,
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository,
			MyEventProducer myEventProducer) {
		super(jwtTokenProvider, passwordEncoder, passwordEncryption, myMongoRepository);
		this.myEventProducer = myEventProducer;
	}

	@Override
	public void updateLoggedOutUsers() {
		// do nothing
	}

	public Boolean updateLoggedOutUsers(List<RevokedToken> revokedTokens) {
		this.jwtTokenService.updateLoggedOutUsers(revokedTokens);
		return Boolean.TRUE;
	}

	@Override
	public MyUser postUserSignin(MyUser myUser) {
		MyUser myUserResult = super.postUserSignin(myUser, false, true);
		if (myUserResult.getUserId() == null) {
			return myUserResult;
		}
		return this.myEventProducer.sendNewUser(myUserResult);
	}

	public MyUser userSigninEvent(Optional<MyUser> myUserOpt) {
		return myUserOpt.map(myUser -> super.postUserSignin(myUser, true, false)).orElse(null);
	}

	@Override
	public Boolean postLogout(String token) {
		String username = this.getTokenUsername(token);
		String uuid = this.getTokenUuid(token);
		RevokedToken revokedToken = new RevokedToken(null, username, uuid, LocalDateTime.now());
		return this.myEventProducer.sendUserLogout(revokedToken) != null;
	}

	public Boolean logoutEvent(Optional<RevokedTokensDto> revokedTokensDtoOpt) {
		return revokedTokensDtoOpt
				.map(revokedTokensDto -> this.updateLoggedOutUsers(revokedTokensDto.getRevokedTokens()))
				.orElse(Boolean.FALSE);
	}
}