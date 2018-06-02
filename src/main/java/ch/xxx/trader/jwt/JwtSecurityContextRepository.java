package ch.xxx.trader.jwt;

import java.util.LinkedList;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		return null;
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		LinkedList<SimpleGrantedAuthority> linkedList = new LinkedList<>();
		linkedList.add(new SimpleGrantedAuthority("USERS"));
		Authentication auth = new JwtAuthenticationToken("user", "password", linkedList);			
		return Mono.just(new SecurityContextImpl(auth));
	}

}
