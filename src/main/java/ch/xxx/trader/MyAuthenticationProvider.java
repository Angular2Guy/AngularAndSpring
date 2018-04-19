package ch.xxx.trader;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import dtos.MyUser;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
	private static final Logger log = LoggerFactory.getLogger(MyAuthenticationProvider.class);
	@Autowired
	private ReactiveMongoOperations operations;
	@Autowired
	private PasswordEncryption passwordEncryption;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();		
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(name));
		MyUser user = operations.findOne(query, MyUser.class).block();
		String encryptedPw = null;
		try {
			encryptedPw = this.passwordEncryption.getEncryptedPassword(password, user.getSalt());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Pw decrytion error: ",e);
		}
		if(encryptedPw == null || !encryptedPw.equals(user.getPassword())) {
			throw new AuthenticationCredentialsNotFoundException("User: "+name+" not found.");
		}
		log.info("User: "+name+" logged in.");
		return new UsernamePasswordAuthenticationToken(
				name, password, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(
		          UsernamePasswordAuthenticationToken.class);
	}

}
