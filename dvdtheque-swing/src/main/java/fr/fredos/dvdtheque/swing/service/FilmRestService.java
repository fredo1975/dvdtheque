package fr.fredos.dvdtheque.swing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.dao.model.object.Film;

@Service
public class FilmRestService {
	private static final String GET_ALL_FILMS_URI = "dvdtheque.web.rest.findAllFilms";
	private final RestTemplate restTemplate;
	@Autowired
    Environment environment;
	public FilmRestService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public List<Film> findAllFilms() {
		return this.restTemplate.getForObject(environment.getRequiredProperty(GET_ALL_FILMS_URI), List.class);
	}
}
