package fr.fredos.dvdtheque.rest.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

//@Component
public class FeignClientInterceptor implements RequestInterceptor{

	private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    
    @Override
    public void apply(RequestTemplate template) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication instanceof JwtAuthenticationToken) {
        	JwtAuthenticationToken details = (JwtAuthenticationToken) authentication;
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, details.getToken().getTokenValue()));
        }
    }
}
