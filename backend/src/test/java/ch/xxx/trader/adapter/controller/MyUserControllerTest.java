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
package ch.xxx.trader.adapter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.trader.domain.common.WebUtils;
import ch.xxx.trader.domain.model.dto.RefreshTokenDto;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.services.MyUserService;
import ch.xxx.trader.usecase.services.JwtTokenService;
import reactor.core.publisher.Mono;

@WebMvcTest(controllers = MyUserController.class
//, includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, JwtTokenService.class })
)
@ComponentScan(basePackages = "ch.xxx.trader")
public class MyUserControllerTest extends BaseControllerTest {
	private static final String TOKEN_KEY = "XQON8wjHynrlb7HyA5IKmjaBN3q2Vh2iPU6n6NIDaiRt6wzXjqwj_m9IHnh60zSCQnaC6Fut37aWBTqYpyFG"
			+ "KHLNCQdpyrTpMcGuUa_kcatWLm18VNJnFQrTdE1IrFXLevCVNLVSCLykujCnaZwPs9EWeraM3cFDx4NLCCDnTX7E46hO1paNHIyNFfNwr4T96fChjISJ"
			+ "XCdxhJddp7dSt_aX7_JUdzJVDh7GhQY-RTDI2sboDWwujg_HUvnMt5huLFdy8c2Fm9RPjEj_nDKluLvbCCNipXCoAy8nGfB0C6DTuwPUK9PgrNe5ON5OKtJEY7rVj4n15InreksN5J0P0A==";
	@MockBean
	private MyUserService myUserService;
	@MockBean
	private JwtTokenService jwtTokenService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void init() {
		Mockito.when(this.myUserService.refreshToken(any(String.class))).thenReturn(Mono.just(new RefreshTokenDto("abc")));
		Mockito.when(this.myUserService.postUserSignin(any(MyUser.class))).thenReturn(Mono.just(this.createMyUser()));
		ReflectionTestUtils.setField(this.jwtTokenService, "validityInMilliseconds", 60000);
		ReflectionTestUtils.setField(this.jwtTokenService, "secretKey", TOKEN_KEY);
	}

	@Test
	public void postUserSigninTest() throws Exception {		
		Mockito.when(this.myUserService.postUserSignin(any(MyUser.class))).thenReturn(Mono.just(this.createMyUser()));
		this.mockMvc.perform(post("/myuser/signin").servletPath("/myuser/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(this.createMyUser()))).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRefreshTokenTest() throws Exception {
		Mockito.when(this.jwtTokenService.createToken(any(String.class), any(List.class))).thenReturn(TOKEN_KEY);
		this.mockMvc.perform(
				get("/myuser/refreshToken").header(WebUtils.AUTHORIZATION, String.format("Bearer %s", TOKEN_KEY))
						.servletPath("/myuser/refreshToken"))
				.andExpect(status().isOk());				
	}
	
	private MyUser createMyUser() {
		MyUser myUser = new MyUser();
		myUser.setUserId("abc");
		myUser.setPassword("pwd");
		myUser.setSalt("salt");
		myUser.setEmail("email");
		myUser.setToken("token");
		return myUser;
	}
}
