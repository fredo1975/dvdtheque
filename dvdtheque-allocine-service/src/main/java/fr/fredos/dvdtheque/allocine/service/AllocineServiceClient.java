package fr.fredos.dvdtheque.allocine.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.allocine.model.Feed;
import fr.fredos.dvdtheque.allocine.model.SearchResults;
import fr.fredos.dvdtheque.service.IFilmService;

@Service
public class AllocineServiceClient {
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceClient.class);
	private static String ALLOCINE_URL_="allocine.url";
	private static String ALLOCINE_QUERY_SEARCH_FILM = "allocine.query.search.film";
	private static String ALLOCINE_QUERY_PARTNER = "allocine.query.partner";
	private static String ALLOCINE_QUERY_FILTER_MOVIE = "allocine.query.filter.movie";
	@Autowired
    Environment environment;
	@Autowired
	protected IFilmService filmService;
	private final RestTemplate restTemplate;
	public AllocineServiceClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }
	
	/**
	 * we're retrieving in Allocine the film with title
	 * @param tmdbId
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public SearchResults retrieveAllocineFeedByTtile(final String title) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(ALLOCINE_URL_)+environment.getRequiredProperty(ALLOCINE_QUERY_SEARCH_FILM)+"?"+"partner="+environment.getRequiredProperty(ALLOCINE_QUERY_PARTNER)+"&format=json"+"&filter="+environment.getRequiredProperty(ALLOCINE_QUERY_FILTER_MOVIE)+"&q="+title, SearchResults.class);
		}catch(org.springframework.web.client.HttpClientErrorException e) {
			logger.error("film "+title+" not found");
		}
		return null;
		
	}
}
