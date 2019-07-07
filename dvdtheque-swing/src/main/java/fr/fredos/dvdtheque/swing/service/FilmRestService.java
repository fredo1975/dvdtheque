package fr.fredos.dvdtheque.swing.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.dao.model.object.Film;
@Service
public class FilmRestService {
	protected Logger logger = LoggerFactory.getLogger(FilmRestService.class);
	private static final String DVDTHEQUE_BASE_URI = "dvdtheque.web.rest.base.url";
	private static final String GET_ALL_FILMS_URI = "dvdtheque.web.rest.findAllFilms";
	private static final String GET_FILM_BY_ID_URI = "dvdtheque.web.rest.findFilmById";
	private static final String GET_TMDB_FILM_URI = "dvdtheque.web.rest.findTmdbFilmByTitre";
	private static final String ADD_TMDB_FILM_URI = "dvdtheque.web.rest.addTmdbFilm";
	private static final String UPDATE_FILM_URI = "dvdtheque.web.rest.updateFilm";
	private static final String CHECK_TMDB_FILM_URI = "dvdtheque.web.rest.checkIfTmdbFilmExists";
	
	private final RestTemplate restTemplate;
	@Autowired
    Environment environment;
	
	public FilmRestService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	public List<Film> findAllFilms() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(this.restTemplate.getForObject(environment.getRequiredProperty(DVDTHEQUE_BASE_URI)+environment.getRequiredProperty(GET_ALL_FILMS_URI), String.class), new TypeReference<List<Film>>(){});
	}
	
	public Set<Film> findTmdbFilmByTitre(final String titre) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(this.restTemplate.getForObject(environment.getRequiredProperty(DVDTHEQUE_BASE_URI)+environment.getRequiredProperty(GET_TMDB_FILM_URI)+titre, String.class), new TypeReference<Set<Film>>(){});
	}
	public Film findFilmById(final Long id) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(this.restTemplate.getForObject(environment.getRequiredProperty(DVDTHEQUE_BASE_URI)+environment.getRequiredProperty(GET_FILM_BY_ID_URI)+id, String.class), new TypeReference<Film>(){});
	}
	public Boolean checkIfTmdbFilmExists(final Long tmdbId) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(this.restTemplate.getForObject(environment.getRequiredProperty(DVDTHEQUE_BASE_URI)+environment.getRequiredProperty(CHECK_TMDB_FILM_URI)+tmdbId, String.class), new TypeReference<Boolean>(){});
	}
	public Film saveTmdbFilm(Long id) {
		try {
			ResponseEntity<Film> response = this.restTemplate
					  .exchange(environment.getRequiredProperty(DVDTHEQUE_BASE_URI)+environment.getRequiredProperty(ADD_TMDB_FILM_URI)+id, HttpMethod.PUT, new HttpEntity<>(id), Film.class);
			return response.getBody();
		}catch(org.springframework.web.client.HttpClientErrorException e) {
			logger.error("film "+id+" not found");
		}
		return null;
	}
	public boolean updateFilm(Film film) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Void> response = this.restTemplate
				  .exchange(environment.getRequiredProperty(DVDTHEQUE_BASE_URI)+environment.getRequiredProperty(UPDATE_FILM_URI)+film.getId(), HttpMethod.PUT, new HttpEntity<>(film,headers), Void.class);
		return response.getStatusCode() == HttpStatus.OK;
	}
}
