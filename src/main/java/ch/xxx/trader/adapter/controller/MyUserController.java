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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.trader.domain.dtos.AuthCheck;
import ch.xxx.trader.domain.dtos.MyUser;
import ch.xxx.trader.usecase.services.MyUserService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/myuser")
public class MyUserController {
	private final MyUserService myUserService;

	public MyUserController(MyUserService myUserService) {
		this.myUserService = myUserService;
	}
	
	@PostMapping("/authorize")
	public Mono<AuthCheck> postAuthorize(@RequestBody AuthCheck authcheck, @RequestHeader Map<String,String> header) {
		return this.myUserService.postAuthorize(authcheck, header);
	}	

	@PostMapping("/signin")
	public Mono<MyUser> postUserSignin(@RequestBody MyUser myUser)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return this.myUserService.postUserSignin(myUser);
	}

	@PostMapping("/logout")
	public Mono<MyUser> postLogout(@RequestBody String hash) {
		return this.myUserService.postLogout(hash);
	}

	@PostMapping("/login")
	public Mono<MyUser> postUserLogin(@RequestBody MyUser myUser,HttpServletRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return this.myUserService.postUserLogin(myUser);		
	}
}
