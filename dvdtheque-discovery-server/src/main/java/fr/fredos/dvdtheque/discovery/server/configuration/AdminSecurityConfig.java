package fr.fredos.dvdtheque.discovery.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class AdminSecurityConfig extends WebSecurityConfigurerAdapter{
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	   http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
	     .and().httpBasic().disable().authorizeRequests()
	     .antMatchers(HttpMethod.GET, "/").hasRole("ADMIN")
	     .antMatchers("/info", "/health").authenticated().anyRequest()
	     .denyAll().and().csrf().disable();
	   }
}
