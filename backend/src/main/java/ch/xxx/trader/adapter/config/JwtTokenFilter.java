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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import ch.xxx.trader.domain.exceptions.AuthenticationException;
import ch.xxx.trader.usecase.services.JwtTokenService;

public class JwtTokenFilter extends BasicAuthenticationFilter {
	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenFilter.class);
	private static final List<String> AUTH_PATHS = List.of("/orderbook", "/authorize", "/refreshToken"); 
	private JwtTokenService jwtTokenProvider;

	public JwtTokenFilter(JwtTokenService jwtTokenProvider, AuthenticationManager authenticationManager) {
		super(authenticationManager);
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String token = this.jwtTokenProvider.resolveToken(httpRequest);		
		UsernamePasswordAuthenticationToken authToken;
		if (AUTH_PATHS.stream().anyMatch(authPath -> httpRequest.getRequestURL().toString().contains(authPath))) { 
		  if(Optional.ofNullable(token).map(myToken -> jwtTokenProvider.validateToken(myToken)).orElse(false)) {
			authToken = Optional.ofNullable(token)
					.map(myToken -> this.jwtTokenProvider.getUsername(token))
					.map(myToken -> {
						UsernamePasswordAuthenticationToken authenticationToken = this.jwtTokenProvider.getUserAuthenticationToken(token);
						authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));						
						return authenticationToken;
					}).orElse(new UsernamePasswordAuthenticationToken(null, null));
		  } else {
			  throw new AuthenticationException("Invalid Token!");
		  }
			//LOG.info(""+authToken.isAuthenticated());
		} else {
			authToken = new UsernamePasswordAuthenticationToken(null, null);
		}
		this.getAuthenticationManager().authenticate(authToken);
		chain.doFilter(request, response);
	}
}
