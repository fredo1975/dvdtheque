package fr.fredos.dvdtheque.tmdb.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OAuth2ClientSecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
	       .authorizeHttpRequests((authz) -> authz.anyRequest().authenticated())
	       .oauth2ResourceServer(resourceServerConfigurer -> resourceServerConfigurer
                   .jwt(jwtConfigurer -> jwtConfigurer
                           .jwtAuthenticationConverter(jwtAuthenticationConverter())));
		return http.build();
	}
	
	@Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }
	
	@Bean
    public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();

        return new Converter<>() {
            @Override
            public Collection<GrantedAuthority> convert(Jwt jwt) {
                Collection<GrantedAuthority> grantedAuthorities = delegate.convert(jwt);

                
                Map<String, Object> map = jwt.getClaims();
                if (map.get("realm_access") == null) {
                	return grantedAuthorities;
                }
                Map<String, Object> realmAccess = (Map<String, Object>) map.get("realm_access");
                if (realmAccess == null) {
                    return grantedAuthorities;
                }
                
                List<String> roles = (List<String>) realmAccess.get("roles");

                final List<SimpleGrantedAuthority> keycloakAuthorities = roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
                grantedAuthorities.addAll(keycloakAuthorities);

                return grantedAuthorities;
            }
        };
    }
}
