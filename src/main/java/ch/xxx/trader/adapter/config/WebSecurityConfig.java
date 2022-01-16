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

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ch.xxx.trader.usecase.services.JwtTokenProvider;
import ch.xxx.trader.usecase.services.MyAuthenticationProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
	    securedEnabled = true,
	    jsr250Enabled = true,
	    prePostEnabled = true
	)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final MyAuthenticationProvider authProvider;
	private final JwtTokenProvider jwtTokenProvider;
	

	public WebSecurityConfig(MyAuthenticationProvider authProvider, JwtTokenProvider jwtTokenProvider) {
		this.authProvider = authProvider;		
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider, this.getAuthenticationManagerBean());
		http.cors().and().csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.authorizeRequests().antMatchers("**/orderbook").authenticated().and()
		.authorizeRequests().anyRequest().anonymous().and()
		.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	
	private AuthenticationManager getAuthenticationManagerBean() {		
		try {
			return super.authenticationManagerBean();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
	}
}
