package fr.fredos.dvdtheque.tmdb.controller;

import static java.lang.String.format;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.common.tmdb.model.Credits;
import fr.fredos.dvdtheque.common.tmdb.model.ReleaseDates;
import fr.fredos.dvdtheque.common.tmdb.model.ReleaseDatesResults;
import fr.fredos.dvdtheque.common.tmdb.model.ReleaseDatesResultsValues;
import fr.fredos.dvdtheque.common.tmdb.model.Results;
import fr.fredos.dvdtheque.common.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.common.utils.DateUtils;

@RestController
@RequestMapping("/dvdtheque-tmdb-service")
public class TmdbServiceController {
	protected Logger logger = LoggerFactory.getLogger(TmdbServiceController.class);
	public static String TMDB_SEARCH_MOVIE_QUERY="themoviedb.search.movie.query";
	public static String TMDB_API_KEY="themoviedb.api.key";
	public static String TMDB_MOVIE_QUERY="themoviedb.movie.query";
	public static String TMDB_POSTER_PATH_URL = "themoviedb.poster.path.url";
	@Autowired
	private Environment environment;
	@Autowired
	private RestTemplate restTemplate;
	
	
	@RolesAllowed({"user","batch"})
	@GetMapping("/retrieveTmdbFilm/byTmdbId")
	ResponseEntity<Results> retrieveTmdbFilm(@RequestParam(name="tmdbId",required = true) Long tmdbId) {
		try {
			Optional<Results> optionalResults = retrieveTmdbSearchResultsById(tmdbId);
			if(optionalResults.isEmpty()) {
				final String msg = "Film with tmbdbId={} already exists";
				logger.error(msg,tmdbId);
				return new ResponseEntity<Results>(HttpStatus.NO_CONTENT);
			}
			return ResponseEntity.ok(optionalResults.get());
		}catch(Exception e) {
			logger.error(format("an error occured while checkIfTmdbFilmExists tmdbid='%s' ", tmdbId),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	/**
	 * we're retrieving in TMDB the film with id tmdbId
	 * @param tmdbId
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public Optional<Results> retrieveTmdbSearchResultsById(final Long tmdbId) {
		try {
			Results results = restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+tmdbId+"?"+"api_key="+environment.getRequiredProperty(TMDB_API_KEY)+"&language=fr", Results.class);
			return Optional.of(results);
			//return restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+tmdbId+"?"+"api_key="+environment.getRequiredProperty(TMDB_API_KEY)+"&language=fr", Results.class);
		}catch(org.springframework.web.client.HttpClientErrorException e) {
			logger.error("film "+tmdbId+" not found");
		}
		return Optional.empty();
	}
	
	@RolesAllowed({"user","batch"})
	@GetMapping("/retrieveTmdbFrReleaseDate/byTmdbId")
	public ResponseEntity<Date> retrieveTmdbFrReleaseDate(@RequestParam(name="tmdbId",required = true)Long tmdbId) throws ParseException {
		try {
			ReleaseDates relDates = restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+tmdbId+"/release_dates?api_key="+environment.getRequiredProperty(TMDB_API_KEY), ReleaseDates.class);
			List<ReleaseDatesResults> releaseDatesResults = relDates.getResults();
			if(CollectionUtils.isNotEmpty(releaseDatesResults)) {
				ReleaseDatesResultsValues releaseDatesResultsValues = null;
				ReleaseDatesResults frReleaseDatesResults = releaseDatesResults.stream().filter(relDate -> relDate.getIso_3166_1().equalsIgnoreCase("FR")).findAny().orElse(null);
				if(frReleaseDatesResults == null) {
					frReleaseDatesResults = releaseDatesResults.stream().filter(relDate -> relDate.getIso_3166_1().equalsIgnoreCase("US")).findAny().orElse(null);
				}
				if(frReleaseDatesResults == null) {
					frReleaseDatesResults = releaseDatesResults.get(0);
				}
				releaseDatesResultsValues = frReleaseDatesResults.getRelease_dates().get(0);
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.TMDB_DATE_PATTERN,Locale.FRANCE);
				return ResponseEntity.ok(sdf.parse(releaseDatesResultsValues.getRelease_date()));
			}
			return ResponseEntity.ok(DateUtils.clearDate(new Date()));
		} catch (RestClientException e) {
			logger.error(format("an error occured while retrieveTmdbFrReleaseDate tmdbid='%s' ", tmdbId),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@RolesAllowed({"user","batch"})
	@GetMapping("/retrieveTmdbCredits/byTmdbId")
	public ResponseEntity<Credits> retrieveTmdbCredits(@RequestParam(name="tmdbId",required = true)Long tmdbId) {
		try {
			return ResponseEntity.ok(restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+tmdbId+"/credits?api_key="+environment.getRequiredProperty(TMDB_API_KEY), Credits.class));
		}catch (RestClientException e) {
			logger.error(format("an error occured while retrieveTmdbCredits tmdbid='%s' ", tmdbId),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@RolesAllowed({"user","batch"})
	@GetMapping("/retrieveTmdbFilmListByTitle/byTitle")
	public ResponseEntity<List<Results>> retrieveTmdbFilmListByTitle(@RequestParam(name="title",required = true)String title){
		Integer firstPage = Integer.valueOf(1);
		Set<Results> results = null;
		try {
			SearchResults searchResults = retrieveTmdbSearchResults(title, firstPage);
			if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
				results = new HashSet<>(searchResults.getTotal_results().intValue());
				addResultsToSet(results, searchResults);
			}
			while(firstPage.intValue() <= searchResults.getTotal_pages()) {
				firstPage = firstPage + Integer.valueOf(1);
				searchResults = retrieveTmdbSearchResults(title, firstPage);
				addResultsToSet(results, searchResults);
			}
			List<Results> resultsSorted = results.stream().filter(film->StringUtils.isNotEmpty(film.getRelease_date())).sorted(new Comparator<>() {
				@Override
				public int compare(Results o1, Results o2) {
					return o2.getRelease_date().compareTo(o1.getRelease_date());
				}
			}).collect(Collectors.toList());
			return ResponseEntity.ok(resultsSorted);
		}catch (RestClientException e) {
			logger.error(format("an error occured while retrieveTmdbFilmListByTitle title='%s' ", title),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	private void addResultsToSet(Set<Results> results, final SearchResults searchResults) {
		results.addAll(searchResults.getResults());
	}
	public SearchResults retrieveTmdbSearchResults(final String titre, Integer page) {
		if(page == null) {
			page = Integer.valueOf(1);
		}
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_SEARCH_MOVIE_QUERY)+"?"+"api_key="+environment.getRequiredProperty(TMDB_API_KEY)+"&query="+titre+"&language=fr&page="+page, SearchResults.class);
		} catch (RestClientException e) {
			throw e;
		}
	}
	
	@RolesAllowed({"user","batch"})
	@GetMapping("/checkIfPosterExists/byPosterPath")
	public ResponseEntity<Boolean> checkIfPosterExists(String posterPath) {
		try {
			byte[] imageBytes = restTemplate.getForObject(posterPath, byte[].class);
			if(imageBytes == null) {
				return ResponseEntity.ok(Boolean.FALSE);
			}
			return ResponseEntity.ok(Boolean.TRUE);
		}catch(HttpClientErrorException | org.springframework.web.client.ResourceAccessException e) {
			logger.error("no poster found for path="+posterPath);
		}
		return ResponseEntity.ok(Boolean.FALSE);
	}
	@RolesAllowed({"user","batch"})
	@GetMapping("/checkIfProfileImageExists/byPosterPath")
	public ResponseEntity<Boolean> checkIfProfileImageExists(String profilePath) {
		try {
			byte[] imageBytes = restTemplate.getForObject(profilePath, byte[].class);
			if(imageBytes == null) {
				return ResponseEntity.ok(Boolean.FALSE);
			}
			return ResponseEntity.ok(Boolean.TRUE);
		}catch(HttpClientErrorException | org.springframework.web.client.ResourceAccessException e) {
			logger.error("no image profile found for profilePath="+profilePath);
		}
		return ResponseEntity.ok(Boolean.FALSE);
	}
}
