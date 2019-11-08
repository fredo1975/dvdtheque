package fr.fredos.dvdtheque.tmdb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
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
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;

@Service
public class TmdbServiceClient {
	protected Logger logger = LoggerFactory.getLogger(TmdbServiceClient.class);
	@Autowired
    Environment environment;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
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
		filmService.updateFilm(toUpdateFilm);
		return toUpdateFilm;
	}
	/**
	 * we're creating a film from a TMDB film
	 * @param tmdbId
	 * @return
	 * @throws Exception 
	 */
	public Film saveTmbdFilm(final Long tmdbId) throws Exception {
		if(this.filmService.checkIfTmdbFilmExists(tmdbId)) {
			throw new Exception("Film with tmbdbId="+tmdbId+" already exists");
		}
		Results results = retrieveTmdbSearchResultsById(tmdbId);
		if(results != null) {
			Film filmToSave = transformTmdbFilmToDvdThequeFilm(null,results, new HashSet<Long>(), true);
			filmToSave.setId(null);
			Dvd dvd = filmService.buildDvd(filmToSave.getAnnee(), null, null, null, DvdFormat.DVD);
			filmToSave.setDvd(dvd);
			Long id = filmService.saveNewFilm(filmToSave);
			filmToSave.setId(id);
			return filmToSave;
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
	public SearchResults retrieveTmdbSearchResults(final String titre) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_SEARCH_MOVIE_QUERY)+"?"+"api_key="+environment.getRequiredProperty(TMDB_API_KEY)+"&query="+titre+"&language=fr", SearchResults.class);
		} catch (RestClientException e) {
			throw e;
		}
	}
	
	/**
	 * create a dvdtheque Film based on a TMBD film
	 * @param film
	 * @param results
	 * @param tmdbFilmAlreadyInDvdthequeSet
	 * @param persistPersonne TODO
	 * @return
	 * @throws ParseException
	 */
	public Film transformTmdbFilmToDvdThequeFilm(Film film,
			final Results results,
			final Set<Long> tmdbFilmAlreadyInDvdthequeSet,
			final boolean persistPersonne) throws ParseException {
		Film transformedfilm = new Film();
		if(film != null && film.getId() != null) {
			transformedfilm.setId(film.getId());
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
		if(StringUtils.isNotEmpty(results.getRelease_date())) {
			transformedfilm.setAnnee(retrieveYearFromReleaseDate(results.getRelease_date()));
		}
		transformedfilm.setPosterPath(environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+results.getPoster_path());
		transformedfilm.setTmdbId(results.getId());
		transformedfilm.setOverview(results.getOverview());
		Credits credits = retrieveTmdbCredits(results.getId());
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
	public Set<Film> retrieveTmdbFilmListToDvdthequeFilmList(final String titre) throws ParseException{
		SearchResults searchResults = retrieveTmdbSearchResults(titre);
		Set<Film> res = null;
		if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
			res = new HashSet<>(searchResults.getResults().size());
			Set<Long> tmdbIds = searchResults.getResults().stream().map(r -> r.getId()).collect(Collectors.toSet());
			Set<Long> tmdbFilmAlreadyInDvdthequeSet = filmService.findAllTmdbFilms(tmdbIds);
			for(Results results : searchResults.getResults()) {
				res.add(transformTmdbFilmToDvdThequeFilm(null,results,tmdbFilmAlreadyInDvdthequeSet, false));
			}
		}
		return res;
	}
	private static int retrieveYearFromReleaseDate(final String dateInStrFormat) throws ParseException {
		DateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		Date releaseDate;
		try {
			releaseDate = sdf.parse(dateInStrFormat);
			Calendar cal = Calendar.getInstance();
			cal.setTime(releaseDate);
			return cal.get(Calendar.YEAR);
		} catch (ParseException e) {
			throw e;
		}
	}
	public Results filterSearchResultsByDateRelease(final Integer annee,
			final List<Results> results) {
		Results res = null;
		if(CollectionUtils.isNotEmpty(results)) {
			res = results.stream().filter(result -> {
				if(StringUtils.isEmpty(result.getRelease_date())) {
					return false;
				}
				try {
					if(retrieveYearFromReleaseDate(result.getRelease_date()) == annee.intValue()) {
						return true;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
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
	
	public List<Crew> retrieveTmdbDirectors(final Credits credits) {
		return credits.getCrew().stream().filter(cred -> cred.getJob().equalsIgnoreCase("Director")).collect(Collectors.toList());
	}
}
