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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.trader.adapter.config.WebSecurityConfig;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.usecase.services.JwtTokenService;
import ch.xxx.trader.usecase.services.MyUserService;
import reactor.core.publisher.Mono;

@WebMvcTest(controllers = MyUserController.class, includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		WebSecurityConfig.class, JwtTokenService.class }))
public class MyUserControllerTest {
	private static final String TOKEN_KEY = "XQON8wjHynrlb7HyA5IKmjaBN3q2Vh2iPU6n6NIDaiRt6wzXjqwj_m9IHnh60zSCQnaC6Fut37aWBTqYpyFG"
			+ "KHLNCQdpyrTpMcGuUa_kcatWLm18VNJnFQrTdE1IrFXLevCVNLVSCLykujCnaZwPs9EWeraM3cFDx4NLCCDnTX7E46hO1paNHIyNFfNwr4T96fChjISJ"
			+ "XCdxhJddp7dSt_aX7_JUdzJVDh7GhQY-RTDI2sboDWwujg_HUvnMt5huLFdy8c2Fm9RPjEj_nDKluLvbCCNipXCoAy8nGfB0C6DTuwPUK9PgrNe5ON5OKtJEY7rVj4n15InreksN5J0P0A==";
	@MockBean
	private MyUserService myUserService;
	@Autowired
	private JwtTokenService jwtTokenService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(this.jwtTokenService, "validityInMilliseconds", 60000);
		ReflectionTestUtils.setField(this.jwtTokenService, "secretKey", TOKEN_KEY);
	}

	@Test
	public void postUserSigninTest() throws Exception {
		MyUser myUser = new MyUser();
		myUser.set_id(new ObjectId());
		myUser.setUserId("XXX");
		Mockito.when(this.myUserService.postUserSignin(any(MyUser.class))).thenReturn(Mono.just(myUser));
		this.mockMvc.perform(post("/myuser/signin").accept(MediaType.APPLICATION_JSON).servletPath("/myuser")
				.content(this.objectMapper.writeValueAsString(myUser))).andExpect(status().isNotFound());
	}
}
