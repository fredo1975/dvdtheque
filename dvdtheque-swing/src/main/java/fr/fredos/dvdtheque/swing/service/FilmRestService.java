package fr.fredos.dvdtheque.swing.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.dao.model.object.Film;
@Service
public class FilmRestService {
	private static final String GET_ALL_FILMS_URI = "dvdtheque.web.rest.findAllFilms";
	private static final String GET_TMDB_FILM_URI = "dvdtheque.web.rest.findTmdbFilmByTitre";
	private final RestTemplate restTemplate;
	@Autowired
    Environment environment;
	
	public FilmRestService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	public List<Film> findAllFilms() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(this.restTemplate.getForObject(environment.getRequiredProperty(GET_ALL_FILMS_URI), String.class), new TypeReference<List<Film>>(){});
	}
	
	public Set<Film> findTmdbFilmByTitre(final String titre) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(this.restTemplate.getForObject(environment.getRequiredProperty(GET_TMDB_FILM_URI)+titre, String.class), new TypeReference<Set<Film>>(){});
	}
}
