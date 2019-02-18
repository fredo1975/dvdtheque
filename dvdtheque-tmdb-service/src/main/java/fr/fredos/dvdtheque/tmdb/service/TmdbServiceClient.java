package fr.fredos.dvdtheque.tmdb.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
	
	public Results filterSearchResultsByDateRelease(final Integer annee,
			final List<Results> results) {
		Results res = null;
		if(CollectionUtils.isNotEmpty(results)) {
			res = results.stream().filter(result -> {
				if(StringUtils.isEmpty(result.getRelease_date())) {
					return false;
				}
				String dateInStrFormat = result.getRelease_date();
				DateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				Date releaseDate;
				try {
					releaseDate = sdf.parse(dateInStrFormat);
					Calendar cal = Calendar.getInstance();
					cal.setTime(releaseDate);
					int year = cal.get(Calendar.YEAR);
					if(year == annee.intValue()) {
						return true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
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
			}else {
				return environment.getRequiredProperty(TMDB_POSTER_PATH_URL)+postersList.get(0).getFile_path();
			}
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
}
