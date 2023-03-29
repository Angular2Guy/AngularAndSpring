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
package ch.xxx.trader.adapter.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import ch.xxx.trader.domain.common.Role;
import ch.xxx.trader.usecase.services.JwtTokenService;

@EnableWebSecurity
@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class WebSecurityConfig {

	private final JwtTokenService jwtTokenProvider;

	public WebSecurityConfig(JwtTokenService jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		HttpSecurity httpSecurity = http.cors().and().csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/*/*/orderbook", "/*/*/*/orderbook")						
						.hasAuthority(Role.USERS.toString()))
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll())				
				.apply(new JwtTokenFilterConfigurer(jwtTokenProvider)).and()
				.headers().contentSecurityPolicy("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';")
				.and().xssProtection().and().and();
		return httpSecurity.build();
	}
}
