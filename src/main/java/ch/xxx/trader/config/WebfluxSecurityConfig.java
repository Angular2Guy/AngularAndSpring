package ch.xxx.trader.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import ch.xxx.trader.jwt.JwtSecurityContextRepository;

//@Configuration
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
//public class WebfluxSecurityConfig {	
//	@Autowired
//	private JwtSecurityContextRepository jwtSecContextRepository;
//	
//	
//	@Bean		
//	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {		
//
//		return http
//				.csrf().disable()
//				.httpBasic().disable()
//				.formLogin().disable()
//				.logout().disable()
//				.securityContextRepository(this.jwtSecContextRepository)
//				.authorizeExchange()
//				.pathMatchers("/**/orderbook").authenticated()
//				.anyExchange().permitAll()
//				.and().build();
//	}
//	
//		
//}
