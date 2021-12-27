package fr.fredos.dvdtheque.rest.config;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ContextConfiguration {
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			MediaType.APPLICATION_JSON.getSubtype(), 
			Charset.forName("utf8"));
	
	/*
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
	    return restTemplate;
	}*/
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(Arrays.asList(
		        new FormHttpMessageConverter(),
		        new OAuth2AccessTokenResponseHttpMessageConverter()));
	    return restTemplate;
	}
	// Create the Okta client registration
    @Bean
    ClientRegistration gatewayClientRegistration(
            @Value("${spring.security.client.registration.keycloak.token-uri}") String token_uri,
            @Value("${spring.security.client.registration.keycloak.client-id}") String client_id,
            @Value("${spring.security.client.registration.keycloak.client-secret}") String client_secret,
            @Value("${spring.security.client.registration.keycloak.authorization-grant-type}") String authorizationGrantType
    ) {
        return ClientRegistration
                .withRegistrationId("keycloak")
                .tokenUri(token_uri)
                .clientId(client_id)
                .clientSecret(client_secret)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
    }

    // Create the client registration repository
	@Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration gatewayClientRegistration) {
        return new InMemoryClientRegistrationRepository(gatewayClientRegistration);
    }
	
	// Create the authorized client service
    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    // Create the authorized client manager and service manager using the
    // beans created and configured above
    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager (
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
