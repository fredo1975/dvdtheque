package fr.fredos.dvdtheque.batch.configuration;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BatchTestConfiguration {
	protected Logger logger = LoggerFactory.getLogger(BatchTestConfiguration.class);
	@Bean
    RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
	
	@Bean
    ClientRegistration dvdthequeClientRegistration(
            @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}") String token_uri,
            @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String client_secret,
            @Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}") String authorizationGrantType
    ) {
		return ClientRegistration
                .withRegistrationId("keycloak")
                .tokenUri(token_uri)
                .clientId("dvdtheque-api")
                .clientSecret(client_secret)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
    }
	
	@Bean
	AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager(ClientRegistration dvdthequeClientRegistration) {
		ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(
				dvdthequeClientRegistration);
		Map<OAuth2AuthorizedClientId, OAuth2AuthorizedClient> authorizedClients = new HashMap<>();
		authorizedClients.put(new OAuth2AuthorizedClientId("keycloak", "batch"), new OAuth2AuthorizedClient(dvdthequeClientRegistration,"batch",
				new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,"cscs", Instant.now(), Instant.MAX)));
		OAuth2AuthorizedClientService authorizedClientService = new InMemoryOAuth2AuthorizedClientService(
				clientRegistrationRepository,authorizedClients);
		return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
				authorizedClientService);
	}
	
}
