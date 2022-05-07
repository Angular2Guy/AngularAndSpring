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
import java.util.Map;

import ch.xxx.trader.domain.model.dto.AuthCheck;
import ch.xxx.trader.domain.model.dto.RefreshTokenDto;
import ch.xxx.trader.domain.model.entity.MyUser;
import reactor.core.publisher.Mono;

public interface MyUserService {
	void updateLoggedOutUsers();
	Mono<AuthCheck> postAuthorize(AuthCheck authcheck, Map<String, String> header);
	Mono<MyUser> postUserSignin(MyUser myUser);
	Mono<Boolean> postLogout(String bearerStr);	
	Mono<MyUser> postUserLogin(MyUser myUser) throws NoSuchAlgorithmException, InvalidKeySpecException;
	RefreshTokenDto refreshToken(String bearerStr);
}
