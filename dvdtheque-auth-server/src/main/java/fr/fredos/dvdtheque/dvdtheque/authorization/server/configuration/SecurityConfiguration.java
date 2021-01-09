package fr.fredos.dvdtheque.dvdtheque.authorization.server.configuration;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.controller.AuthenticationController;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.filter.AuthTokenFilter;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.service.JdbcUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	protected Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	@Autowired
	private JdbcUserDetailsService jdbcUserDetailsService;
	
	@Bean
    public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
    }
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	/*
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**");
    }*/
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> jdbcUserDetailsService.loadUserByUsername(username)).passwordEncoder(passwordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	// Enable CORS and disable CSRF
        http.cors().and().csrf().disable();

        // Set session management to stateless
        http = http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and();
    	
        http.authorizeRequests().antMatchers("/",
                "/favicon.ico",
                "/**/*.png",
                "/**/*.gif",
                "/**/*.svg",
                "/**/*.jpg",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js")
                .permitAll()
            .antMatchers("/api/auth/**")
                .permitAll()
            .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**")
                .permitAll().anyRequest().authenticated();
        
        
        // Set unauthorized requests exception handler
        http = http
            .exceptionHandling()
            .authenticationEntryPoint(
                (request, response, ex) -> {
                	response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ex.getMessage()
                    );
                }
            )
            .and();
/*
        // Set permissions on endpoints
        http.authorizeRequests()
            // Our public endpoints
            .antMatchers("/dvdtheque/**").permitAll()
            .antMatchers(HttpMethod.GET, "/dvdtheque/**").permitAll()
            .antMatchers(HttpMethod.POST, "/dvdtheque").permitAll()
            // Our private endpoints
            .anyRequest().authenticated().and()
            .oauth2ResourceServer()
            .jwt(jwt -> jwt.decoder(JwtDecoders.fromIssuerLocation(issuerUri)));
*/
        // Add JWT token filter
        http.addFilterBefore(
        		authenticationJwtTokenFilter(),
            UsernamePasswordAuthenticationFilter.class
        );
    }
    
    // Used by spring security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
