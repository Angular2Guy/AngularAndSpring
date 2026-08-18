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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.services.MyUserRepository;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
	private static final Logger log = LoggerFactory.getLogger(MyAuthenticationProvider.class);
	private final MyUserRepository myUserRepository;
	private final PasswordEncoder passwordEncoder;
	
	public MyAuthenticationProvider(MyUserRepository myUserRepository, PasswordEncoder passwordEncoder) {
		this.myUserRepository = myUserRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = Optional.ofNullable(authentication.getCredentials()).map(pwd -> pwd.toString()).orElse(null);		
		Optional<MyUser> userOpt = this.myUserRepository.findByUserId(name);
		if(userOpt.isEmpty()) {
			return new UsernamePasswordAuthenticationToken(null, null); 
			//throw new BadCredentialsException("User not found");
		}
		MyUser user = userOpt.get();
		if(!this.passwordEncoder.matches(password, user.getPassword())) {
			return new UsernamePasswordAuthenticationToken(null, null); 
			//throw new AuthenticationCredentialsNotFoundException("User: "+name+" not found.");
		}
		log.info("User: "+name+" logged in.");
		return new UsernamePasswordAuthenticationToken(
				name, password, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(
		          UsernamePasswordAuthenticationToken.class);
	}

}
