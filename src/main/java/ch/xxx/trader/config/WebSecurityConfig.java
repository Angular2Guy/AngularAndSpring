package ch.xxx.trader.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import ch.xxx.trader.MyAuthenticationProvider;

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {	
	
	@Autowired
    private MyAuthenticationProvider authProvider;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {	
		http.httpBasic();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
		http.authorizeRequests().anyRequest().permitAll().anyRequest().anonymous();
		http.antMatcher("/**/orderbook").authorizeRequests().anyRequest().authenticated(); 
		http.csrf().disable();
	}
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {  
        auth.authenticationProvider(authProvider);
    }		
	
}
