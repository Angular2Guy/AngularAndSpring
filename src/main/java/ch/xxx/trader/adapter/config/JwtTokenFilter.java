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

import ch.xxx.trader.usecase.services.JwtTokenProvider;

public class JwtTokenFilter extends BasicAuthenticationFilter {
	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenFilter.class);
	private JwtTokenProvider jwtTokenProvider;

	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
		super(authenticationManager);
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String token = jwtTokenProvider.resolveToken(httpRequest);
		UsernamePasswordAuthenticationToken authToken;
		if ((httpRequest.getRequestURL().toString().contains("/orderbook") || httpRequest.getRequestURL().toString().contains("/authorize"))  && Optional.ofNullable(token).map(myToken -> jwtTokenProvider.validateToken(myToken)).orElse(false)) {
			authToken = Optional.ofNullable(token)
					.map(myToken -> jwtTokenProvider.getUserAuthenticationToken(token)).map(myToken -> {
						myToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
						return myToken;
					}).orElse(new UsernamePasswordAuthenticationToken(null, null));
			//LOG.info(""+authToken.isAuthenticated());
		} else {
			authToken = new UsernamePasswordAuthenticationToken(null, null);
		}
		this.getAuthenticationManager().authenticate(authToken);
		chain.doFilter(request, response);
	}
}
