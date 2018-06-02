package ch.xxx.trader.jwt;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority{
	USERS;

	@Override
	public String getAuthority() {		
		return this.name();
	}
	
}
