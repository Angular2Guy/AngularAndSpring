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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.PasswordEncryption;
import ch.xxx.trader.domain.model.entity.MyUser;
import reactor.core.publisher.Mono;

@Profile("!kafka & !prod-kafka")
@Service
public class MyUserServiceDb extends MyUserServiceBean implements MyUserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyUserServiceDb.class);
	
	public MyUserServiceDb(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
			PasswordEncryption passwordEncryption, MyMongoRepository myMongoRepository) {
		super(jwtTokenProvider, passwordEncoder, passwordEncryption, myMongoRepository);
	}

	@Override
	public Mono<MyUser> postUserSignin(MyUser myUser) {
		return super.postUserSignin(myUser, true, true);
	}
}
