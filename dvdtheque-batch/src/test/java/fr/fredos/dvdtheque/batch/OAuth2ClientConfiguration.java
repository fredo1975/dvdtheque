package fr.fredos.dvdtheque.batch;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class OAuth2ClientConfiguration {
	//private final RestTemplateBuilder restTemplateBuilder;
	@Autowired
	ClientRegistrationRepository registrations;
	@Bean
    RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
	@Bean
    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager (
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
	@Bean
    ClientRegistrationRepository clientRegistrationRepository(ClientRegistration dvdthequeClientRegistration) {
        return new InMemoryClientRegistrationRepository(dvdthequeClientRegistration);
    }
	
	@Bean
    ClientRegistration dvdthequeClientRegistration() {
        return ClientRegistration
                .withRegistrationId("keycloak")
                .tokenUri("http://fake")
                .clientId("dvd")
                .clientSecret("secret")
                .authorizationGrantType(new AuthorizationGrantType("client_credentials"))
                .build();
    }
	@Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
	/*
	@Autowired
	ClientRegistrationRepository registrations;
	@Bean
    ClientRegistration dvdthequeClientRegistration() {
        return ClientRegistration
                .withRegistrationId("keycloak")
                .tokenUri("http://fake")
                .clientId("dvd")
                .clientSecret("secret")
                .authorizationGrantType(new AuthorizationGrantType("client_credentials"))
                .build();
    }

    // Create the client registration repository
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration dvdthequeClientRegistration) {
        return new InMemoryClientRegistrationRepository(dvdthequeClientRegistration);
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
    }*/
}
