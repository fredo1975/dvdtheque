package fr.fredos.dvdtheque.tmdb.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.dto.ActeurDto;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
import fr.fredos.dvdtheque.service.dto.RealisateurDto;
import fr.fredos.dvdtheque.tmdb.model.Cast;
import fr.fredos.dvdtheque.tmdb.model.Credits;
import fr.fredos.dvdtheque.tmdb.model.Crew;
import fr.fredos.dvdtheque.tmdb.model.ImagesResults;
import fr.fredos.dvdtheque.tmdb.model.Posters;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;

@Service
public class TmdbServiceClient {
	@Autowired
    Environment environment;
	private final RestTemplate restTemplate;
	private static String TMDB_SEARCH_MOVIE_QUERY="themoviedb.search.movie.query";
	private static String TMDB_API_KEY="themoviedb.api.key";
	private static String TMDB_SEARCH_IMAGES_QUERY="themoviedb.search.images.query";
	private static String TMDB_POSTER_PATH_URL = "themoviedb.poster.path.url";
	
	public TmdbServiceClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }
	
	public SearchResults retrieveTmdbSearchResults(String titre) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_SEARCH_MOVIE_QUERY)+"?"+"api_key="+environment.getRequiredProperty(TMDB_API_KEY)+"&query="+titre, SearchResults.class);
		} catch (RestClientException e) {
			throw e;
		}
	}
	private Film transformTmdbFilmToDvdThequeFilm(Results results) throws ParseException {
		Film film = new Film();
		film.setTitre(results.getTitle());
		film.setTitreO(results.getOriginal_title());
		film.setAnnee(retrieveYearFromReleaseDate(results.getRelease_date()));
		film.setPosterPath(environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+results.getPoster_path());
		film.setId(Integer.valueOf(results.getId().toString()));
		Credits credits = retrieveTmdbCredits(results.getId());
		if(CollectionUtils.isNotEmpty(credits.getCast())) {
			int i=0;
			for(Cast cast : credits.getCast()) {
				Personne personne = new Personne();
				personne.setId(Integer.valueOf(cast.getCast_id()));
				personne.setNom(StringUtils.upperCase(cast.getName()));
				film.getActeurs().add(personne);
				if(i++==5) {
					break;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(credits.getCrew())) {
			Personne realisateur = new Personne();
			realisateur.setNom(StringUtils.upperCase(retrieveTmdbDirector(credits)));
			film.getRealisateurs().add(realisateur);
		}
		return film;
	}
	public Set<Film> retrieveTmdbFilmListToDvdthequeFilmList(String titre) throws ParseException{
		SearchResults searchResults = retrieveTmdbSearchResults(titre);
		Set<Film> res = null;
		if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
			res = new HashSet<>(searchResults.getResults().size());
			for(Results results : searchResults.getResults()) {
				res.add(transformTmdbFilmToDvdThequeFilm(results));
			}
		}
		return res;
	}
	private static int retrieveYearFromReleaseDate(String dateInStrFormat) throws ParseException {
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
	
	public ImagesResults retrieveTmdbImagesResults(Long idFilm) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_SEARCH_IMAGES_QUERY)+idFilm+"/images?api_key="+environment.getRequiredProperty(TMDB_API_KEY), ImagesResults.class);
		} catch (RestClientException e) {
			throw e;
		}
	}
	public String retrieveTmdbFrPosterPathUrl(ImagesResults imagesResults) {
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
	public String retrieveTmdbFrPosterPath(ImagesResults imagesResults) {
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
	
	public Credits retrieveTmdbCredits(Long idFilm) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(TMDB_SEARCH_IMAGES_QUERY)+idFilm+"/credits?api_key="+environment.getRequiredProperty(TMDB_API_KEY), Credits.class);
		} catch (RestClientException e) {
			throw e;
		}
	}
	
	public String retrieveTmdbDirector(Credits credits) {
		String res = null;
		if(CollectionUtils.isNotEmpty(credits.getCrew())) {
			List<Crew> directors = credits.getCrew().stream().filter(cred -> cred.getJob().equalsIgnoreCase("Director")).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(directors)) {
				return directors.iterator().next().getName();
			}
		}
		return res;
	}
}
