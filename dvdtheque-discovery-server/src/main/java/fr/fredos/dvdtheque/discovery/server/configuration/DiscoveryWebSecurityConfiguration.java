package fr.fredos.dvdtheque.discovery.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(1)
public class DiscoveryWebSecurityConfiguration extends WebSecurityConfigurerAdapter{
	@Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
        .withUser("admin").password("{noop}admin")
        .authorities("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .authorizeRequests()
              .anyRequest().authenticated()
              .and()
              .httpBasic();
        
        /*
        http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        .and().requestMatchers().antMatchers("/eureka/**")
        .and().authorizeRequests().antMatchers("/eureka/**")
        .hasRole("ADMIN").anyRequest().denyAll().and()
        .httpBasic().and().csrf().disable();*/
         
    }
}
