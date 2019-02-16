package fr.fredos.dvdtheque.tmdb.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TmdbServiceClient {

	private final RestTemplate restTemplate;
	
	public TmdbServiceClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }
	
	
}
