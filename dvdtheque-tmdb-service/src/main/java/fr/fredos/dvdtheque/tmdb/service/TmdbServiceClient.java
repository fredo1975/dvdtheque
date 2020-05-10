package fr.fredos.dvdtheque.tmdb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.allocine.service.AllocineServiceClient;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.utils.DateUtils;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.tmdb.model.Cast;
import fr.fredos.dvdtheque.tmdb.model.Credits;
import fr.fredos.dvdtheque.tmdb.model.Crew;
import fr.fredos.dvdtheque.tmdb.model.Genres;
import fr.fredos.dvdtheque.tmdb.model.ImagesResults;
import fr.fredos.dvdtheque.tmdb.model.Posters;
import fr.fredos.dvdtheque.tmdb.model.ReleaseDates;
import fr.fredos.dvdtheque.tmdb.model.ReleaseDatesResults;
import fr.fredos.dvdtheque.tmdb.model.ReleaseDatesResultsValues;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;

@Service
public class TmdbServiceClient {
	protected Logger logger = LoggerFactory.getLogger(TmdbServiceClient.class);
	private final static String TMDB_DATE_PATTERN = "yyyy-MM-dd";
	@Autowired
    Environment environment;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
    private AllocineServiceClient allocineServiceClient;
	private final RestTemplate restTemplate;
	private static String TMDB_SEARCH_MOVIE_QUERY="themoviedb.search.movie.query";
	private static String TMDB_API_KEY="themoviedb.api.key";
	private static String TMDB_MOVIE_QUERY="themoviedb.movie.query";
	private static String TMDB_POSTER_PATH_URL = "themoviedb.poster.path.url";
	private static String NB_ACTEURS="batch.save.nb.acteurs";
	private Map<Integer,Genres> genresById;
	public Map<Integer,Genres> getGenresById() {
		return genresById;
	}
	public TmdbServiceClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }
	@PostConstruct
	public void loadGenres() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("genres.json");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1));
		List<Genres> l = objectMapper.readValue(bufferedReader, new TypeReference<List<Genres>>(){});
		genresById = new HashMap<Integer, Genres>(l.size());
		for(Genres genres : l) {
			genresById.put(genres.getId(), genres);
		}
	}
	/**
	 * we're updating all informations with tmdbId in DB for film idFilm
	 * @param tmdbId
	 * @param idFilm
	 * @throws Exception 
	 */
	public Film replaceFilm(final Long tmdbId,final Film film) throws Exception {
		if(this.filmService.checkIfTmdbFilmExists(tmdbId)) {
			throw new Exception("Film with tmbdbId="+tmdbId+" already exists");
		}
		Results results = retrieveTmdbSearchResultsById(tmdbId);
		Film toUpdateFilm = transformTmdbFilmToDvdThequeFilm(film,results, new HashSet<Long>(), true);
		if(toUpdateFilm != null) {
			toUpdateFilm.setOrigine(film.getOrigine());
			filmService.updateFilm(toUpdateFilm);
			return toUpdateFilm;
		}
		return null;
	}
	/**
	 * we're creating a film from a TMDB film
	 * @param tmdbId
	 * @param filmOrigine
	 * @return
	 * @throws Exception 
	 */
	public Film saveTmbdFilm(final Long tmdbId, FilmOrigine filmOrigine) throws Exception {
		if(this.filmService.checkIfTmdbFilmExists(tmdbId)) {
			throw new Exception("Film with tmbdbId="+tmdbId+" already exists");
		}
		Results results = retrieveTmdbSearchResultsById(tmdbId);
		if(results != null) {
			Film filmToSave = transformTmdbFilmToDvdThequeFilm(null,results, new HashSet<Long>(), true);
			if(filmToSave != null) {
				filmToSave.setId(null);
				filmToSave.setOrigine(filmOrigine);
				if(FilmOrigine.DVD.equals(filmOrigine)) {
					Dvd dvd = filmService.buildDvd(filmToSave.getAnnee(), new Integer(2), null, null, DvdFormat.DVD, null);
					dvd.setRipped(true);
					dvd.setDateRip(new Date());
					filmToSave.setDvd(dvd);
				}
				filmToSave.setDateInsertion(DateUtils.clearDate(new Date()));
				allocineServiceClient.addCritiquesPresseToFilm(filmToSave);
				Long id = filmService.saveNewFilm(filmToSave);
				filmToSave.setId(id);
				return filmToSave;
			}
		}
		return null;
	}
	/**
	 * we're retrieving in TMDB the film with id tmdbId
	 * @param tmdbId
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public Results retrieveTmdbSearchResultsById(final Long tmdbId) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+tmdbId+"?"+"api_key="+environment.getRequiredProperty(TMDB_API_KEY)+"&language=fr", Results.class);
		}catch(org.springframework.web.client.HttpClientErrorException e) {
			logger.error("film "+tmdbId+" not found");
		}
		return null;
		
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
	public void retrieveFilmImage(Film film) {
		Results results = retrieveTmdbSearchResultsById(film.getTmdbId());
		film.setPosterPath(environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+results.getPoster_path());
	}
	public void retrieveFilmImage(final Film film, final Results results) {
		film.setPosterPath(environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+results.getPoster_path());
	}
	
	public boolean checkIfPosterExists(Film film) {
		try {
			byte[] imageBytes = restTemplate.getForObject(film.getPosterPath(), byte[].class);
			if(imageBytes == null) {
				return false;
			}
			return true;
		}catch(HttpClientErrorException | org.springframework.web.client.ResourceAccessException e) {
			logger.error("no poster found for film id="+film.getId());
		}
		return false;
	}
	
	public boolean checkIfProfileImageExists(Personne personne) {
		try {
			byte[] imageBytes = restTemplate.getForObject(personne.getProfilePath(), byte[].class);
			if(imageBytes == null) {
				return false;
			}
			return true;
		}catch(HttpClientErrorException | org.springframework.web.client.ResourceAccessException e) {
			logger.error("no image profile found for personne id="+personne.getId());
		}
		return false;
	}
	
	public void retrieveFilmImagesWhenNotExist(Film film) {
		boolean posterExists = checkIfPosterExists(film);
		Results results = null;
		if(!posterExists) {
			results = retrieveTmdbSearchResultsById(film.getTmdbId());
			retrieveFilmImage(film,results);
		}
		Credits credits = null;
		for(Personne acteur : film.getActeurs()) {
			boolean exists = checkIfProfileImageExists(acteur);
			if(!exists) {
				if(results == null) {
					results = retrieveTmdbSearchResultsById(film.getTmdbId());
				}
				if(credits == null) {
					credits = retrieveTmdbCredits(results.getId());
				}
				for(Cast cast : credits.getCast()) {
					if(cast.getName().equalsIgnoreCase(acteur.getNom())) {
						acteur = personneService.createOrRetrievePersonne(StringUtils.upperCase(cast.getName()), environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+cast.getProfile_path());
						acteur.setProfilePath(environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+cast.getProfile_path());
						personneService.updatePersonne(acteur);
					}
				}
			}
		}
	}
	
	/**
	 * create a dvdtheque Film based on a TMBD film
	 * @param film
	 * @param results
	 * @param tmdbFilmAlreadyInDvdthequeSet
	 * @param persistPersonne
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	public Film transformTmdbFilmToDvdThequeFilm(Film film,
			final Results results,
			final Set<Long> tmdbFilmAlreadyInDvdthequeSet,
			final boolean persistPersonne) throws ParseException, IOException {
		Film transformedfilm = new Film();
		if(film != null && film.getId() != null) {
			transformedfilm.setId(film.getId());
			transformedfilm.setDateInsertion(film.getDateInsertion());
		}
		if(film == null) {
			transformedfilm.setId(results.getId());
		}
		if(CollectionUtils.isNotEmpty(tmdbFilmAlreadyInDvdthequeSet) && tmdbFilmAlreadyInDvdthequeSet.contains(results.getId())) {
			transformedfilm.setAlreadyInDvdtheque(true);
		}
		transformedfilm.setTitre(StringUtils.upperCase(results.getTitle()));
		transformedfilm.setTitreO(StringUtils.upperCase(results.getOriginal_title()));
		if(film != null && film.getDvd() != null) {
			transformedfilm.setDvd(film.getDvd());
		}
		Date releaseDate = null;
		try {
			releaseDate = retrieveTmdbFrReleaseDate(results.getId());
		}catch(RestClientException e) {
			logger.error(e.getMessage()+" for id="+results.getId());
			SimpleDateFormat sdf = new SimpleDateFormat(TMDB_DATE_PATTERN,Locale.FRANCE);
			if(StringUtils.isNotEmpty(results.getRelease_date())) {
				releaseDate = sdf.parse(results.getRelease_date());
			}else {
				releaseDate = sdf.parse("2000/01/01");
			}
		}
		transformedfilm.setAnnee(retrieveYearFromReleaseDate(releaseDate));
		transformedfilm.setDateSortie(DateUtils.clearDate(releaseDate));
		transformedfilm.setPosterPath(environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+results.getPoster_path());
		filmService.saveImageToFilmPosterAsByteArray(transformedfilm.getPosterPath(), transformedfilm);
		transformedfilm.setTmdbId(results.getId());
		transformedfilm.setOverview(results.getOverview());
		
		try {
			retrieveAndSetCredits(persistPersonne, results, transformedfilm);
		}catch(Exception e) {
			logger.error(e.getMessage()+" for id="+results.getId()+" won't be displayed");
			return null;
		}
		
		transformedfilm.setRuntime(results.getRuntime());
		List<Genres> genres = results.getGenres();
		if(CollectionUtils.isNotEmpty(genres)){
			for (Genres g : genres) {
				Genres _g = this.genresById.get(g.getId());
				if(_g != null) {
					Genre genre = filmService.findGenre(_g.getId());
					if(genre == null) {
						genre = filmService.saveGenre(new Genre(_g.getId(),_g.getName()));
					}else {
						genre = filmService.attachToSession(genre);
					}
					transformedfilm.getGenres().add(genre);
				}else {
					logger.error("genre "+g.getName()+" not found in loaded genres");
				}
			}
		}
		transformedfilm.setVu(false);
		if(StringUtils.isNotEmpty(results.getHomepage())) {
			transformedfilm.setHomepage(results.getHomepage());
		}
		return transformedfilm;
	}
	
	private void retrieveAndSetCredits(final boolean persistPersonne, final Results results, final Film transformedfilm) {
		Credits credits = null;
		credits = retrieveTmdbCredits(results.getId());
		if(CollectionUtils.isNotEmpty(credits.getCast())) {
			int i=1;
			for(Cast cast : credits.getCast()) {
				Personne personne = null;
				if(!persistPersonne) {
					personne = personneService.buildPersonne(StringUtils.upperCase(cast.getName()), environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+cast.getProfile_path());
					personne.setId(Long.valueOf(cast.getCast_id()));
				}else {
					personne = personneService.createOrRetrievePersonne(StringUtils.upperCase(cast.getName()), environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+cast.getProfile_path());
				}
				transformedfilm.getActeurs().add(personne);
				if(i++==Integer.parseInt(environment.getRequiredProperty(NB_ACTEURS))) {
					break;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(credits.getCrew())) {
			List<Crew> crew = retrieveTmdbDirectors(credits);
			for(Crew c : crew) {
				Personne realisateur = null;
				if(!persistPersonne) {
					realisateur = personneService.buildPersonne(StringUtils.upperCase(c.getName()), null);
					realisateur.setId(RandomUtils.nextLong());
				}else {
					realisateur = personneService.createOrRetrievePersonne(StringUtils.upperCase(c.getName()), null);
				}
				transformedfilm.getRealisateurs().add(realisateur);
			}
		}
	}
	private void addResultsToSet(Set<Results> results, final SearchResults searchResults) {
		results.addAll(searchResults.getResults());
	}
	public Set<Film> retrieveTmdbFilmListToDvdthequeFilmList(final String titre) throws ParseException, IOException{
		Set<Film> films = null;
		Set<Results> results = null;
		Integer firstPage = Integer.valueOf(1);
		SearchResults searchResults = retrieveTmdbSearchResults(titre, firstPage);
		if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
			results = new HashSet<>(searchResults.getTotal_results().intValue());
			addResultsToSet(results, searchResults);
		}
		while(firstPage.intValue() <= searchResults.getTotal_pages()) {
			firstPage = firstPage + Integer.valueOf(1);
			searchResults = retrieveTmdbSearchResults(titre, firstPage);
			addResultsToSet(results, searchResults);
		}
		
		if(CollectionUtils.isNotEmpty(results)) {
			films = new HashSet<>(results.size());
			Set<Long> tmdbIds = results.stream().map(r -> r.getId()).collect(Collectors.toSet());
			Set<Long> tmdbFilmAlreadyInDvdthequeSet = filmService.findAllTmdbFilms(tmdbIds);
			for(Results res : results) {
				Film transformedFilm = transformTmdbFilmToDvdThequeFilm(null,res,tmdbFilmAlreadyInDvdthequeSet, false);
				if(transformedFilm != null) {
					films.add(transformedFilm);
				}
			}
		}
		return films;
	}
	private static int retrieveYearFromReleaseDate(final Date relDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(relDate);
		return cal.get(Calendar.YEAR);
	}
	
	public Results filterSearchResultsByDateRelease(final Integer annee,
			final List<Results> results) {
		Results res = null;
		if(CollectionUtils.isNotEmpty(results)) {
			res = results.stream().filter(result -> {
				if(StringUtils.isEmpty(result.getRelease_date())) {
					return false;
				}
				/*
				try {
					if(retrieveYearFromReleaseDate(result.getRelease_date()) == annee.intValue()) {
						return true;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}*/
				return false;
			}).findAny().orElse(null);
			if(res==null) {
				res = results.get(0);
			}
		}
		return res;
	}
	
	public ImagesResults retrieveTmdbImagesResults(final Long idFilm) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+idFilm+"/images?api_key="+environment.getRequiredProperty(TMDB_API_KEY), ImagesResults.class);
		} catch (RestClientException e) {
			throw e;
		}
	}
	public String retrieveTmdbFrPosterPathUrl(final ImagesResults imagesResults) {
		final String res = null;
		List<Posters> postersList = imagesResults.getPosters();
		if(CollectionUtils.isNotEmpty(postersList)) {
			List<Posters> posters = postersList.stream().filter(poster -> poster.getIso_639_1()!=null && poster.getIso_639_1().equalsIgnoreCase("fr")).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(posters)) {
				return environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+posters.get(0).getFile_path();
			}
			List<Posters> posters2 = postersList.stream().filter(poster -> poster.getIso_639_1()!=null && poster.getIso_639_1().equalsIgnoreCase("en")).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(posters2)) {
				return environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+posters2.get(0).getFile_path();
			}
			return environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+postersList.get(0).getFile_path();
		}
		return res;
	}
	public String retrieveTmdbFrPosterPath(final ImagesResults imagesResults) {
		final String res = null;
		List<Posters> postersList = imagesResults.getPosters();
		if(CollectionUtils.isNotEmpty(postersList)) {
			List<Posters> posters = postersList.stream().filter(poster -> poster.getIso_639_1()!=null && poster.getIso_639_1().equalsIgnoreCase("fr")).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(posters)) {
				return posters.get(0).getFile_path();
			}else {
				return postersList.get(0).getFile_path();
			}
		}
		return res;
	}
	
	public Credits retrieveTmdbCredits(final Long idFilm) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+idFilm+"/credits?api_key="+environment.getRequiredProperty(TMDB_API_KEY), Credits.class);
		} catch (RestClientException e) {
			throw e;
		}
	}
	
	public Date retrieveTmdbFrReleaseDate(final Long idFilm) throws ParseException {
		try {
			ReleaseDates relDates = restTemplate.getForObject(environment.getRequiredProperty(TMDB_MOVIE_QUERY)+idFilm+"/release_dates?api_key="+environment.getRequiredProperty(TMDB_API_KEY), ReleaseDates.class);
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
				SimpleDateFormat sdf = new SimpleDateFormat(TMDB_DATE_PATTERN,Locale.FRANCE);
				return sdf.parse(releaseDatesResultsValues.getRelease_date());
			}
			return DateUtils.clearDate(new Date());
		} catch (RestClientException e) {
			throw e;
		}
	}
	
	public List<Crew> retrieveTmdbDirectors(final Credits credits) {
		return credits.getCrew().stream().filter(cred -> cred.getJob().equalsIgnoreCase("Director")).collect(Collectors.toList());
	}
}
