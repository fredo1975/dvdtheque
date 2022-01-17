package fr.fredos.dvdtheque.integration.config;

import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
@TestConfiguration
public class ContextConfiguration {
	@Bean
    public KeycloakClientRequestFactory keycloakClientRequestFactory() {
		return new KeycloakClientRequestFactory();
	}

    @Bean
    public KeycloakRestTemplate keycloakRestTemplate() {
        return new KeycloakRestTemplate(keycloakClientRequestFactory());
    }

}
