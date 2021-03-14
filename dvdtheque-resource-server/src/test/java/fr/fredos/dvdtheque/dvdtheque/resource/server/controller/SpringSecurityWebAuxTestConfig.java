package fr.fredos.dvdtheque.dvdtheque.resource.server.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {
	@Bean
    @Primary
    public UserDetailsService userDetailsService() {
		List<SimpleGrantedAuthority> l = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"));
		
		UserDetails basicUser = new org.springframework.security.core.userdetails.User("user","password",l);
		UserDetails userUser = new org.springframework.security.core.userdetails.User("fredo","password",l);
		
        return new InMemoryUserDetailsManager(Arrays.asList(
        		basicUser, userUser
        ));
    }
}
