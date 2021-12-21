package fr.fredos.dvdtheque.rest.controller;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.fredos.dvdtheque.common.tmdb.model.Results;
import fr.fredos.dvdtheque.rest.config.CustomFeignClientConfiguration;

@FeignClient(name="api-gateway-service", configuration = CustomFeignClientConfiguration.class)
public interface TmdbServiceFeignClient {
	@RequestMapping(path = "/dvdtheque-tmdb-service/retrieveTmdbFilmListByTitle/byTitle", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	ResponseEntity<Set<Results>> retrieveTmdbFilmListByTitle(@RequestParam(name="title",required = true) String title);
	
	@RequestMapping(path = "/dvdtheque-tmdb-service/retrieveTmdbFilm/byTmdbId", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	ResponseEntity<Results> retrieveTmdbFilm(@RequestParam(name="tmdbId",required = true) Long tmdbId);

}
