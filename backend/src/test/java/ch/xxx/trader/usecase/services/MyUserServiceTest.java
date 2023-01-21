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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.xxx.trader.domain.common.PasswordEncryption;
import ch.xxx.trader.domain.model.dto.RefreshTokenDto;
import ch.xxx.trader.domain.model.entity.MyUser;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MyUserServiceTest {
	@Mock
	private JwtTokenService jwtTokenService;
	@Mock
	private PasswordEncryption passwordEncryption;
	@Mock
	private MyMongoRepository myMongoRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
	private MyUserServiceDb myUserService;
	
	@SuppressWarnings("unchecked")
	@Test
	public void postUserSigninTest() throws Exception {
		MyUser myUser = new MyUser();
		myUser.setUserId("XXX");
		myUser.setPassword("ABCDEFG123");
		Mockito.when(this.myMongoRepository.findOne(isA(Query.class), isA(Class.class))).thenReturn(Mono.just(myUser));
		Mockito.when(this.myMongoRepository.save(any(MyUser.class))).thenReturn(Mono.just(myUser));
		Optional<MyUser> result = this.myUserService.postUserSignin(myUser).blockOptional();
		Assertions.assertTrue(result.isPresent());		
	}
	
	@Test
	public void refreshTokenTestSuccess() throws Exception {
		final String DUMMY_TOKEN = "ABC";
		Mockito.when(this.jwtTokenService.resolveToken(any(String.class))).thenReturn(Optional.of(DUMMY_TOKEN));
		Mockito.when(this.jwtTokenService.refreshToken(any(String.class))).thenReturn(DUMMY_TOKEN);
		RefreshTokenDto result = this.myUserService.refreshToken(DUMMY_TOKEN);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(DUMMY_TOKEN, result.getRefreshToken());
	}
	
	@Test
	public void refreshTokenTestFailed() throws Exception {
		final String DUMMY_TOKEN = "ABC";
		Mockito.when(this.jwtTokenService.resolveToken(any(String.class))).thenReturn(Optional.empty());
		Assertions.assertThrows(Exception.class, () -> this.myUserService.refreshToken(DUMMY_TOKEN));
	}
}
