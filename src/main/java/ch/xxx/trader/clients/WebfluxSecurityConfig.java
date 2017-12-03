package ch.xxx.trader.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import ch.xxx.trader.PasswordEncryption;
import reactor.core.publisher.Mono;

//@EnableWebSecurity
//@EnableWebFluxSecurity
public class WebfluxSecurityConfig {
	
	@Autowired
	private ReactiveMongoOperations operations;
	@Autowired
	private PasswordEncryption passwordEncryption;
	
	@Bean
	public ReactiveUserDetailsService userDetailsRepository() {
		return new ReactiveUserDetailsService() {
			
			@Override
			public Mono<UserDetails> findByUsername(String username) {
				Query query = new Query();
				query.addCriteria(Criteria.where("userId").is(username));
				return operations.findOne(query, UserDetails.class);
			}
		};
	}			
	
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange()
				.anyExchange().authenticated()
				.and()
			.httpBasic();
		return http.build();
	}
}
