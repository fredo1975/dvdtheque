package fr.fredos.dvdtheque.tmdb.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class ContextConfiguration {
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
