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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.xxx.trader.usecase.services.JwtTokenProvider;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenFilter.class);
	private JwtTokenProvider jwtTokenProvider;

	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = jwtTokenProvider.resolveToken(request);
		if (Optional.ofNullable(token).map(myToken -> jwtTokenProvider.validateToken(myToken)).orElse(false)) {
			Optional<UsernamePasswordAuthenticationToken> myOpt = Optional.ofNullable(token)
					.map(myToken -> jwtTokenProvider.getUserAuthenticationToken(token)).map(myToken -> {
						myToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						return myToken;
					});
			myOpt.ifPresent(myToken -> SecurityContextHolder.getContext().setAuthentication(myToken));			
			//LOG.info(""+SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
		}
		filterChain.doFilter(request, response);
	}

}
