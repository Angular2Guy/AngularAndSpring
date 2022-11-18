package ch.xxx.trader.adapter.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ch.xxx.trader.usecase.services.JwtTokenService;

public class JwtTokenFilterConfigurer  extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	private JwtTokenService jwtTokenProvider;

	  public JwtTokenFilterConfigurer(JwtTokenService jwtTokenProvider) {
	    this.jwtTokenProvider = jwtTokenProvider;
	  }

	  @Override
	  public void configure(HttpSecurity http) throws Exception {
	    JwtTokenFilter customFilter = new JwtTokenFilter(jwtTokenProvider);
	    http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}
}