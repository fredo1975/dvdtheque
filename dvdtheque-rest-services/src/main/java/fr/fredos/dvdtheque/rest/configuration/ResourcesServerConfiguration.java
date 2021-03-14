package fr.fredos.dvdtheque.rest.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableResourceServer
public class ResourcesServerConfiguration extends ResourceServerConfigurerAdapter {
	@Autowired
	private DataSource dataSource;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		TokenStore tokenStore = new JdbcTokenStore(dataSource);
		resources.resourceId("api").tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/dvdtheque/public/**")
		.permitAll()
		//.access("hasRole('ROLE_USER')")
				.antMatchers(HttpMethod.POST, "/dvdtheque/secured/**").access("hasRole('USER')")
				.antMatchers(HttpMethod.PATCH, "/dvdtheque/secured/**").access("hasRole('USER')")
				.antMatchers(HttpMethod.PUT, "/dvdtheque/secured/**").access("hasRole('USER')")
				.antMatchers(HttpMethod.DELETE, "/dvdtheque/secured/**").access("hasRole('USER')").and()
				.headers().addHeaderWriter((request, response) -> {
					response.addHeader("Access-Control-Allow-Origin", "*");
					if (request.getMethod().equals("OPTIONS")) {
						response.setHeader("Access-Control-Allow-Methods",
								request.getHeader("Access-Control-Request-Method"));
						response.setHeader("Access-Control-Allow-Headers",
								request.getHeader("Access-Control-Request-Headers"));
					}
				});
	}
}