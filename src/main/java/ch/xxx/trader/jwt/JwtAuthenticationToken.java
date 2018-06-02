package ch.xxx.trader.jwt;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 3947891062000505349L;

	private final Object principal;
	private Object credentials;
	
	public JwtAuthenticationToken(Object principal, Object credentials,Collection<? extends GrantedAuthority> authorities) {
		super(authorities);		
		this.principal = principal;
		this.credentials = credentials;
		this.setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

}
