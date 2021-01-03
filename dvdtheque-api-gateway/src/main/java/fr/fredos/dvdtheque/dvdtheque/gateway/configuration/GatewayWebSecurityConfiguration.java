package fr.fredos.dvdtheque.dvdtheque.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class GatewayWebSecurityConfiguration extends WebSecurityConfigurerAdapter{
	@Bean
	public ServerCodecConfigurer serverCodecConfigurer() {
	   return ServerCodecConfigurer.create();
	}
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	    auth.inMemoryAuthentication().withUser("user").password("password")
	      .roles("USER").and().withUser("admin").password("admin")
	      .roles("ADMIN");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.authorizeRequests().antMatchers("/dvdtheque/**")
	      .permitAll().antMatchers("/eureka/**").hasRole("ADMIN")
	      .anyRequest().authenticated().and().formLogin().and()
	      .logout().permitAll().logoutSuccessUrl("/dvdtheque/**")
	      .permitAll().and().csrf().disable();
	}
}
