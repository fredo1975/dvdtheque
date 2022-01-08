package fr.fredos.dvdtheque.rest.controller;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmDisplayType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.exceptions.DvdthequeServerRestException;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.common.tmdb.model.Cast;
import fr.fredos.dvdtheque.common.tmdb.model.Credits;
import fr.fredos.dvdtheque.common.tmdb.model.Crew;
import fr.fredos.dvdtheque.common.tmdb.model.Genres;
import fr.fredos.dvdtheque.common.tmdb.model.Results;
import fr.fredos.dvdtheque.common.tmdb.model.TmdbServiceCommon;
import fr.fredos.dvdtheque.common.utils.DateUtils;
import fr.fredos.dvdtheque.rest.allocine.model.DvdBuilder;
import fr.fredos.dvdtheque.rest.dao.domain.Dvd;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import fr.fredos.dvdtheque.rest.file.util.MultipartFileUtil;
import fr.fredos.dvdtheque.rest.model.ExcelFilmHandler;
import fr.fredos.dvdtheque.rest.service.IFilmService;
import fr.fredos.dvdtheque.rest.service.IPersonneService;
import fr.fredos.dvdtheque.rest.service.model.FilmListParam;

@RestController
@ComponentScan({ "fr.fredos.dvdtheque" })
@RequestMapping("/dvdtheque-service")
public class FilmController {
	protected Logger logger = LoggerFactory.getLogger(FilmController.class);
	public static String TMDB_SERVICE_URL = "tmdb-service.url";
	public static String TMDB_SERVICE_BY_TITLE = "tmdb-service.byTitle";
	public static String TMDB_SERVICE_RELEASE_DATE = "tmdb-service.release-date";
	public static String TMDB_SERVICE_CREDITS = "tmdb-service.get-credits";
	public static String TMDB_SERVICE_RESULTS = "tmdb-service.get-results";
	public static String DVDTHEQUE_BATCH_SERVICE_URL = "dvdtheque-batch-service.url";
	public static String DVDTHEQUE_BATCH_SERVICE_IMPORT = "dvdtheque-batch-service.import";
	private static String NB_ACTEURS = "batch.save.nb.acteurs";
	@Autowired
	Environment environment;
	@Autowired
	private IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
	private ExcelFilmHandler excelFilmHandler;
	@Autowired
	private MultipartFileUtil multipartFileUtil;
	@Value("${eureka.instance.instance-id}")
	private String instanceId;
	@Value("${limit.film.size}")
	private int limitFilmSize;
	@Autowired
	private KeycloakRestTemplate keycloakRestTemplate;
	private Map<Integer, Genres> genresById;

	public Map<Integer, Genres> getGenresById() {
		return genresById;
	}

	public void loadGenres() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("genres.json");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1));
		List<Genres> l = objectMapper.readValue(bufferedReader, new TypeReference<List<Genres>>() {
		});
		genresById = new HashMap<Integer, Genres>(l.size());
		for (Genres genres : l) {
			genresById.put(genres.getId(), genres);
		}
	}

	public FilmController() throws JsonParseException, JsonMappingException, IOException {
		loadGenres();
	}

	@RolesAllowed("user")
	@GetMapping("/films/byPersonne")
	ResponseEntity<Personne> findPersonne(@RequestParam(name = "nom", required = false) String nom) {
		try {
			return ResponseEntity.ok(personneService.findPersonneByName(nom));
		} catch (Exception e) {
			logger.error(format("an error occured while findPersonne nom='%s' ", nom), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed({ "user", "batch" })
	@GetMapping("/films")
	ResponseEntity<List<Film>> findAllFilms(@RequestParam(name = "displayType", required = false) String displayType) {
		try {
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType, 0, FilmOrigine.TOUS);
			return ResponseEntity.ok(filmService.findAllFilms(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(format("an error occured while findAllFilms displayType='%s' ", displayType), e);
		}
		return ResponseEntity.badRequest().build();
	}

	@RolesAllowed("user")
	@GetMapping("/films/genres")
	ResponseEntity<List<Genre>> findAllGenres() {
		try {
			return ResponseEntity.ok(filmService.findAllGenres());
		} catch (Exception e) {
			logger.error(format("an error occured while findAllGenres"), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed({ "user", "batch" })
	@PutMapping("/films/cleanAllfilms")
	void cleanAllFilms() {
		filmService.cleanAllFilms();
	}

	@RolesAllowed("user")
	@GetMapping("/films/byTitre/{titre}")
	ResponseEntity<Film> findFilmByTitre(@PathVariable String titre) {
		try {
			return ResponseEntity.ok(filmService.findFilmByTitre(titre));
		} catch (Exception e) {
			logger.error(format("an error occured while findFilmByTitre titre='%s' ", titre), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed({ "user", "batch" })
	@GetMapping("/films/byOrigine/{origine}")
	ResponseEntity<List<Film>> findAllFilmsByOrigine(@PathVariable String origine,
			@RequestParam(name = "displayType", required = false) String displayType) {
		logger.debug("findAllFilmsByOrigine - instanceId=" + instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType, this.limitFilmSize,
					filmOrigine);
			return ResponseEntity.ok(filmService.findAllFilmsByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(format("an error occured while findAllFilmsByOrigine origine='%s' and displayType='%s' ",
					origine, displayType), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed({ "user", "batch" })
	@GetMapping("/filmListParam/byOrigine/{origine}")
	ResponseEntity<FilmListParam> findFilmListParamByFilmDisplayTypeParam(@PathVariable String origine,
			@RequestParam(name = "displayType", required = false) String displayType) {
		logger.debug("findFilmListParamByFilmDisplayType - instanceId=" + instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType, this.limitFilmSize,
					filmOrigine);
			return ResponseEntity.ok(filmService.findFilmListParamByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(format(
					"an error occured while findFilmListParamByFilmDisplayTypeParam origine='%s' and displayType='%s' ",
					origine, displayType), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@GetMapping("/films/tmdb/byTitre/{titre}")
	ResponseEntity<List<Film>> findTmdbFilmByTitre(@PathVariable String titre) throws ParseException {
		List<Film> films = null;
		try {
			ResponseEntity<Set<Results>> resultsResponse = keycloakRestTemplate.exchange(
					environment.getRequiredProperty(TMDB_SERVICE_URL)
							+ environment.getRequiredProperty(TMDB_SERVICE_BY_TITLE) + "?title=" + titre,
					HttpMethod.GET, null, new ParameterizedTypeReference<Set<Results>>() {});
			if (resultsResponse != null && CollectionUtils.isNotEmpty(resultsResponse.getBody())) {
				Set<Results> results = resultsResponse.getBody();
				films = new ArrayList<>(results.size());
				Set<Long> tmdbIds = results.stream().map(r -> r.getId()).collect(Collectors.toSet());
				Set<Long> tmdbFilmAlreadyInDvdthequeSet = filmService.findAllTmdbFilms(tmdbIds);
				for (Results res : results) {
					Film transformedFilm = transformTmdbFilmToDvdThequeFilm(null, res, tmdbFilmAlreadyInDvdthequeSet,
							false);
					if (transformedFilm != null) {
						films.add(transformedFilm);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(films)) {
				Collections.sort(films);
			}
			return ResponseEntity.ok(films);
		} catch (Exception e) {
			logger.error(format("an error occured while findTmdbFilmByTitre titre='%s' ", titre), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@GetMapping("/films/byId/{id}")
	ResponseEntity<Film> findFilmById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(filmService.findFilm(id));
		} catch (Exception e) {
			logger.error(format("an error occured while findFilmById id='%s' ", id), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@GetMapping("/films/byTmdbId/{tmdbid}")
	ResponseEntity<Boolean> checkIfTmdbFilmExists(@PathVariable Long tmdbid) {
		try {
			return ResponseEntity.ok(filmService.checkIfTmdbFilmExists(tmdbid));
		} catch (Exception e) {
			logger.error(format("an error occured while checkIfTmdbFilmExists tmdbid='%s' ", tmdbid), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@GetMapping("/realisateurs")
	ResponseEntity<List<Personne>> findAllRealisateurs() {
		try {
			return ResponseEntity.ok(filmService
					.findAllRealisateurs(new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0, FilmOrigine.TOUS)));
		} catch (Exception e) {
			logger.error(format("an error occured while findAllRealisateurs"), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@GetMapping("/realisateurs/byOrigine/{origine}")
	ResponseEntity<List<Personne>> findAllRealisateursByOrigine(@PathVariable String origine,
			@RequestParam(name = "displayType", required = false) String displayType) {
		logger.info("findAllRealisateursByOrigine - instanceId=" + instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType, this.limitFilmSize,
					filmOrigine);
			if (FilmOrigine.TOUS.equals(filmOrigine)) {
				return ResponseEntity.ok(filmService.findAllRealisateurs(filmDisplayTypeParam));
			}
			return ResponseEntity.ok(filmService.findAllRealisateursByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(
					format("an error occured while findAllRealisateursByOrigine origine='%s' and displayType='%s' ",
							origine, displayType),
					e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	// @PreAuthorize("hasAuthority('user')")
	@GetMapping("/acteurs")
	ResponseEntity<List<Personne>> findAllActeurs() {
		try {
			return ResponseEntity.ok(
					filmService.findAllActeurs(new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0, FilmOrigine.TOUS)));
		} catch (Exception e) {
			logger.error(format("an error occured while findAllActeurs"), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@GetMapping("/acteurs/byOrigine/{origine}")
	ResponseEntity<List<Personne>> findAllActeursByOrigine(@PathVariable String origine,
			@RequestParam(name = "displayType", required = false) String displayType) {
		logger.info("findAllActeursByOrigine - instanceId=" + instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType, this.limitFilmSize,
					filmOrigine);
			if (FilmOrigine.TOUS.equals(filmOrigine)) {
				return ResponseEntity.ok(filmService.findAllActeurs(filmDisplayTypeParam));
			}
			return ResponseEntity.ok(filmService.findAllActeursByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(format("an error occured while findAllActeursByOrigine origine='%s' and displayType='%s' ",
					origine, displayType), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@GetMapping("/personnes")
	ResponseEntity<List<Personne>> findAllPersonne() {
		try {
			return ResponseEntity.ok(personneService.findAllPersonne());
		} catch (Exception e) {
			logger.error(format("an error occured while findAllPersonne"), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@PutMapping("/films/tmdb/{tmdbId}")
	ResponseEntity<Film> replaceFilm(@RequestBody Film film, @PathVariable Long tmdbId) throws Exception {
		try {
			Film filmOptional = filmService.findFilm(film.getId());
			if (filmOptional == null) {
				return ResponseEntity.notFound().build();
			}
			Results results = keycloakRestTemplate.getForObject(
					environment.getRequiredProperty(TMDB_SERVICE_URL)
							+ environment.getRequiredProperty(TMDB_SERVICE_RESULTS) + "?tmdbId=" + tmdbId,
					Results.class);
			Film toUpdateFilm = transformTmdbFilmToDvdThequeFilm(film, results, new HashSet<Long>(), true);
			if (toUpdateFilm != null) {
				toUpdateFilm.setOrigine(film.getOrigine());
				filmService.updateFilm(toUpdateFilm);
				return ResponseEntity.ok(toUpdateFilm);
			}
			return ResponseEntity.ok(toUpdateFilm);
		} catch (Exception e) {
			logger.error("an error occured while replacing film tmdbId=" + tmdbId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed({ "user", "batch" })
	@PutMapping("/transformTmdbFilmToDvdThequeFilm/tmdb/{tmdbId}")
	ResponseEntity<Film> transformTmdbFilmToDvdThequeFilm(@RequestBody Results results, @PathVariable Long tmdbId) {
		Film filmToSave = null;
		try {
			filmToSave = transformTmdbFilmToDvdThequeFilm(null, results, new HashSet<Long>(), true);
			return ResponseEntity.ok(filmToSave);
		} catch (Exception e) {
			logger.error("an error occured while transformTmdbFilmToDvdThequeFilm film tmdbId=" + tmdbId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * create a dvdtheque Film based on a TMBD film
	 * 
	 * @param film
	 * @param results
	 * @param tmdbFilmAlreadyInDvdthequeSet
	 * @param persistPersonne
	 * @return
	 * @throws ParseException
	 */
	public Film transformTmdbFilmToDvdThequeFilm(Film film, final Results results,
			final Set<Long> tmdbFilmAlreadyInDvdthequeSet, final boolean persistPersonne) throws ParseException {
		Film transformedfilm = new Film();
		if (film != null && film.getId() != null) {
			transformedfilm.setId(film.getId());
			transformedfilm.setDateInsertion(film.getDateInsertion());
		}
		if (film == null) {
			transformedfilm.setId(results.getId());
		}
		if (CollectionUtils.isNotEmpty(tmdbFilmAlreadyInDvdthequeSet)
				&& tmdbFilmAlreadyInDvdthequeSet.contains(results.getId())) {
			transformedfilm.setAlreadyInDvdtheque(true);
		}
		transformedfilm.setTitre(StringUtils.upperCase(results.getTitle()));
		transformedfilm.setTitreO(StringUtils.upperCase(results.getOriginal_title()));
		if (film != null && film.getDvd() != null) {
			transformedfilm.setDvd(film.getDvd());
		}
		Date releaseDate = null;
		try {
			// releaseDate = retrieveTmdbFrReleaseDate(results.getId());
			releaseDate = keycloakRestTemplate.getForObject(
					environment.getRequiredProperty(TMDB_SERVICE_URL)
							+ environment.getRequiredProperty(TMDB_SERVICE_RELEASE_DATE) + "?tmdbId=" + results.getId(),
					Date.class);
		} catch (RestClientException e) {
			logger.error(e.getMessage() + " for id=" + results.getId());
			SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.TMDB_DATE_PATTERN, Locale.FRANCE);
			if (StringUtils.isNotEmpty(results.getRelease_date())) {
				releaseDate = sdf.parse(results.getRelease_date());
			} else {
				releaseDate = sdf.parse("2000-01-01");
			}
		}
		transformedfilm.setAnnee(retrieveYearFromReleaseDate(releaseDate));
		transformedfilm.setDateSortie(DateUtils.clearDate(releaseDate));
		transformedfilm.setPosterPath(
				environment.getRequiredProperty(TmdbServiceCommon.TMDB_POSTER_PATH_URL) + results.getPoster_path());
		transformedfilm.setTmdbId(results.getId());
		transformedfilm.setOverview(results.getOverview());
		try {
			retrieveAndSetCredits(persistPersonne, results, transformedfilm);
		} catch (Exception e) {
			logger.error(e.getMessage() + " for id=" + results.getId() + " won't be displayed");
			return null;
		}

		transformedfilm.setRuntime(results.getRuntime());
		List<Genres> genres = results.getGenres();
		if (CollectionUtils.isNotEmpty(genres)) {
			for (Genres g : genres) {
				Genres _g = this.genresById.get(g.getId());
				if (_g != null) {
					Genre genre = filmService.findGenre(_g.getId());
					if (genre == null) {
						genre = filmService.saveGenre(new Genre(_g.getId(), _g.getName()));
					} else {
						genre = filmService.attachToSession(genre);
					}
					transformedfilm.getGenre().add(genre);
				} else {
					logger.error("genre " + g.getName() + " not found in loaded genres");
				}
			}
		}
		if (StringUtils.isNotEmpty(results.getHomepage())) {
			transformedfilm.setHomepage(results.getHomepage());
		}
		return transformedfilm;
	}

	private void retrieveAndSetCredits(final boolean persistPersonne, final Results results,
			final Film transformedfilm) {
		Credits credits = keycloakRestTemplate.getForObject(
				environment.getRequiredProperty(TMDB_SERVICE_URL)
						+ environment.getRequiredProperty(TMDB_SERVICE_CREDITS) + "?tmdbId=" + results.getId(),
				Credits.class);
		if (CollectionUtils.isNotEmpty(credits.getCast())) {
			int i = 1;
			for (Cast cast : credits.getCast()) {
				Personne personne = null;
				if (!persistPersonne) {
					personne = personneService.buildPersonne(StringUtils.upperCase(cast.getName()),
							environment.getRequiredProperty(TmdbServiceCommon.TMDB_POSTER_PATH_URL)
									+ cast.getProfile_path());
					personne.setId(Long.valueOf(cast.getCast_id()));
				} else {
					personne = personneService.createOrRetrievePersonne(StringUtils.upperCase(cast.getName()),
							environment.getRequiredProperty(TmdbServiceCommon.TMDB_POSTER_PATH_URL)
									+ cast.getProfile_path());
				}
				transformedfilm.getActeur().add(personne);
				if (i++ == Integer.parseInt(environment.getRequiredProperty(NB_ACTEURS))) {
					break;
				}
			}
		}
		if (CollectionUtils.isNotEmpty(credits.getCrew())) {
			List<Crew> crew = retrieveTmdbDirectors(credits);
			for (Crew c : crew) {
				Personne realisateur = null;
				if (!persistPersonne) {
					realisateur = personneService.buildPersonne(StringUtils.upperCase(c.getName()), null);
					realisateur.setId(RandomUtils.nextLong());
				} else {
					realisateur = personneService.createOrRetrievePersonne(StringUtils.upperCase(c.getName()), null);
				}
				transformedfilm.getRealisateur().add(realisateur);
			}
		}
	}

	public List<Crew> retrieveTmdbDirectors(final Credits credits) {
		return credits.getCrew().stream().filter(cred -> cred.getJob().equalsIgnoreCase("Director"))
				.collect(Collectors.toList());
	}

	private static int retrieveYearFromReleaseDate(final Date relDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(relDate);
		return cal.get(Calendar.YEAR);
	}

	@RolesAllowed("user")
	@PutMapping("/films/update/{id}")
	ResponseEntity<Film> updateFilm(@RequestBody Film film, @PathVariable Long id) {
		try {
			Film filmOptional = filmService.findFilm(id);
			if (filmOptional == null) {
				return ResponseEntity.notFound().build();
			}
			// handle date rip
			if (filmOptional.getDvd() != null && !filmOptional.getDvd().isRipped() && film.getDvd() != null
					&& film.getDvd().isRipped()) {
				film.getDvd().setDateRip(new Date());
			}
			Film mergedFilm = filmService.updateFilm(film);
			return ResponseEntity.ok(mergedFilm);
		} catch (Exception e) {
			logger.error("an error occured while updating film id=" + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@PutMapping("/films/remove/{id}")
	ResponseEntity<Film> removeFilm(@PathVariable Long id) {
		try {
			Film filmOptional = filmService.findFilm(id);
			if (filmOptional == null) {
				return ResponseEntity.notFound().build();
			}
			filmService.removeFilm(filmOptional);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("an error occured while removing film id=" + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@PutMapping("/films/cleanCaches")
	ResponseEntity<Void> cleanCaches() {
		try {
			filmService.cleanAllCaches();
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("an error occured while cleaning all caches", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@PutMapping("/films/retrieveImage/{id}")
	ResponseEntity<Film> retrieveFilmImage(@PathVariable Long id) {
		try {
			Film film = filmService.findFilm(id);
			if (film == null) {
				return ResponseEntity.notFound().build();
			}
			Results results = keycloakRestTemplate.getForObject(
					environment.getRequiredProperty(TMDB_SERVICE_URL) + "?tmdbId=" + film.getTmdbId(), Results.class);
			film.setPosterPath(
					environment.getRequiredProperty(TmdbServiceCommon.TMDB_POSTER_PATH_URL) + results.getPoster_path());
			return ResponseEntity.ok(filmService.updateFilm(film));
		} catch (Exception e) {
			logger.error("an error occured while retrieving image for film id=" + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@PutMapping("/films/retrieveAllImages")
	ResponseEntity<Void> retrieveAllFilmImages() {
		Results results = null;
		try {
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0,
					FilmOrigine.TOUS);
			List<Film> films = filmService.findAllFilms(filmDisplayTypeParam);
			for (Film film : films) {
				Boolean posterExists = keycloakRestTemplate.getForObject(
						environment.getRequiredProperty(TMDB_SERVICE_URL) + "?posterPath=" + film.getPosterPath(),
						Boolean.class);
				if (!posterExists) {
					results = keycloakRestTemplate.getForObject(
							environment.getRequiredProperty(TMDB_SERVICE_URL) + "?tmdbId=" + film.getTmdbId(),
							Results.class);
					film.setPosterPath(environment.getRequiredProperty(TmdbServiceCommon.TMDB_POSTER_PATH_URL)
							+ results.getPoster_path());
				}
				filmService.updateFilm(film);
			}
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("an error occured while retrieving all images", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed({ "batch" })
	@PostMapping("/films/saveProcessedFilm")
	ResponseEntity<Film> saveProcessedFilm(@RequestBody Film film) throws Exception {
		Film filmToSave = null;
		try {
			Results results = keycloakRestTemplate.getForObject(environment.getRequiredProperty(TMDB_SERVICE_URL)
					+ environment.getRequiredProperty(FilmController.TMDB_SERVICE_RESULTS) + "?tmdbId="
					+ film.getTmdbId(), Results.class);
			if (results != null) {
				filmToSave = transformTmdbFilmToDvdThequeFilm(film, 
						results, 
						new HashSet<Long>(), 
						true);
				if (filmToSave != null) {
					filmToSave.setId(null);
					filmToSave.setOrigine(film.getOrigine());
					filmToSave.setId(null);
					if(film.getDateInsertion() != null) {
						filmToSave.setDateInsertion(film.getDateInsertion());
					}else {
						filmToSave.setDateInsertion(DateUtils.clearDate(new Date()));
					}
					filmToSave.setVu(film.isVu());
					Long id = filmService.saveNewFilm(filmToSave);
					filmToSave.setId(id);
					return ResponseEntity.ok(filmToSave);
				}
			}
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			logger.error("an error occured while saving film tmdbId=" + film.getTmdbId(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@RolesAllowed({ "user", "batch" })
	@PutMapping("/films/save/{tmdbId}")
	ResponseEntity<Film> saveFilm(@PathVariable Long tmdbId, @RequestBody String origine) throws Exception {
		Film filmToSave = null;
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			if (this.filmService.checkIfTmdbFilmExists(tmdbId)) {
				return ResponseEntity.noContent().build();
			}
			Results results = keycloakRestTemplate.getForObject(environment.getRequiredProperty(TMDB_SERVICE_URL)
					+ environment.getRequiredProperty(FilmController.TMDB_SERVICE_RESULTS) + "?tmdbId=" + tmdbId,
					Results.class);
			if (results != null) {
				filmToSave = transformTmdbFilmToDvdThequeFilm(null, results, new HashSet<Long>(), true);
				if (filmToSave != null) {
					filmToSave.setId(null);
					filmToSave.setOrigine(filmOrigine);
					if (FilmOrigine.DVD.equals(filmOrigine)) {
						Dvd dvd = filmService.buildDvd(filmToSave.getAnnee(), Integer.valueOf(2), null, null,
								DvdFormat.DVD, null);
						dvd.setRipped(true);
						dvd.setDateRip(new Date());
						filmToSave.setDvd(dvd);
					}
					filmToSave.setDateInsertion(DateUtils.clearDate(new Date()));
					Long id = filmService.saveNewFilm(filmToSave);
					filmToSave.setId(id);
				}
			}
			if (filmToSave == null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(filmToSave);
		} catch (Exception e) {
			logger.error("an error occured while saving film tmdbId=" + tmdbId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed({ "user", "batch" })
	@PostMapping("/films/buildDvd")
	ResponseEntity<Dvd> buildDvd(@RequestBody DvdBuilder dvdBuilder) throws Exception {
		try {
			Dvd dvd = filmService.buildDvd(dvdBuilder.getFilmToSave().getAnnee(), dvdBuilder.getZonedvd(), null, null,
					StringUtils.isNotEmpty(dvdBuilder.getFilmFormat()) ? DvdFormat.valueOf(dvdBuilder.getFilmFormat())
							: null,
					dvdBuilder.getDateSortieDvd());
			return ResponseEntity.ok(dvd);
		} catch (Exception e) {
			logger.error("an error occured while building dvd ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RolesAllowed("user")
	@PutMapping("/personnes/byId/{id}")
	ResponseEntity<Object> updatePersonne(@RequestBody Personne p, @PathVariable Long id) {
		Personne personne = personneService.findByPersonneId(id);
		if (personne == null) {
			return ResponseEntity.notFound().build();
		}
		if (StringUtils.isNotEmpty(p.getNom())) {
			personne.setNom(StringUtils.upperCase(p.getNom()));
		}
		personneService.updatePersonne(personne);
		logger.info(personne.toString());
		return ResponseEntity.noContent().build();
	}

	@RolesAllowed("user")
	@PostMapping("/films/import")
	ResponseEntity<Void> importFilmList(@RequestParam("file") MultipartFile file) {
		File resFile = null;
		try {
			resFile = this.multipartFileUtil.createFileToImport(file);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		HttpEntity<?> request = new HttpEntity<>(resFile.getAbsolutePath());
		ResponseEntity<String> resultsResponse = keycloakRestTemplate.exchange(
				environment.getRequiredProperty(DVDTHEQUE_BATCH_SERVICE_URL)
						+ environment.getRequiredProperty(DVDTHEQUE_BATCH_SERVICE_IMPORT),
				HttpMethod.POST, request, String.class);
		logger.info(resultsResponse.getBody());
		/*
		 * try { JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		 * jobParametersBuilder.addString("INPUT_FILE_PATH", resFile.getAbsolutePath());
		 * jobParametersBuilder.addLong("TIMESTAMP",new Date().getTime());
		 * jobLauncher.run(importFilmsJob, jobParametersBuilder.toJobParameters()); }
		 * catch (JobExecutionAlreadyRunningException |
		 * JobInstanceAlreadyCompleteException | JobParametersInvalidException |
		 * JobRestartException e) {
		 * logger.error("an error occured while importFilmList",e); return
		 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
		 */
		return ResponseEntity.noContent().build();
	}

	@RolesAllowed("user")
	@PostMapping("/films/export")
	ResponseEntity<byte[]> exportFilmList(@RequestBody String origine)
			throws DvdthequeServerRestException, IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLanguage(Locale.FRANCE);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		LocalDateTime localDate = LocalDateTime.now();
		String fileName = "ListeDVDExport" + "-" + localDate.getSecond() + "-" + origine + ".xlsx";
		try {
			List<Film> list = null;
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			if (FilmOrigine.TOUS.equals(filmOrigine)) {
				FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0,
						FilmOrigine.TOUS);
				list = filmService.findAllFilms(filmDisplayTypeParam);
			} else {
				list = filmService.findAllFilmsByCriteria(
						new FilmFilterCriteriaDto(null, null, null, null, null, null, filmOrigine));
			}
			if (list == null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			byte[] excelContent = this.excelFilmHandler.createByteContentFromFilmList(list);
			headers.setContentType(
					MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			headers.setContentLength(excelContent.length);
			return new ResponseEntity<byte[]>(excelContent, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("an error occured while exporting film list", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
