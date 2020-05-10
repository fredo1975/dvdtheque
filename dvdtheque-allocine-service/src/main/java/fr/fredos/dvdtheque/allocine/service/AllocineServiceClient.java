package fr.fredos.dvdtheque.allocine.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.allocine.model.Review;
import fr.fredos.dvdtheque.allocine.model.SearchResults;
import fr.fredos.dvdtheque.dao.model.object.CritiquesPresse;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;

@Service
public class AllocineServiceClient {
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceClient.class);
	private static String ALLOCINE_URL_="allocine.url";
	private static String ALLOCINE_QUERY_SEARCH_FILM = "allocine.query.search.film";
	private static String ALLOCINE_QUERY_PARTNER = "allocine.query.partner";
	private static String ALLOCINE_QUERY_FILTER_MOVIE = "allocine.query.filter.movie";
	private static String ALLOCINE_QUERY_SEARCH_REVIEW_LIST = "allocine.query.reviewlist";
	private static String ALLOCINE_QUERY_FILTER_DESK_PRESS = "allocine.query.filter.desk-press";
	@Autowired
    Environment environment;
	@Autowired
	protected IFilmService filmService;
	private final RestTemplate restTemplate;
	public AllocineServiceClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }
	
	/**
	 * we're retrieving in Allocine the film with title
	 * @param tmdbId
	 * @return
	 */
	public SearchResults retrieveAllocineMovieFeedByTitle(final String title) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(ALLOCINE_URL_)+environment.getRequiredProperty(ALLOCINE_QUERY_SEARCH_FILM)+"?"+"partner="+environment.getRequiredProperty(ALLOCINE_QUERY_PARTNER)+"&format=json"+"&filter="+environment.getRequiredProperty(ALLOCINE_QUERY_FILTER_MOVIE)+"&q="+title, SearchResults.class);
		}catch(org.springframework.web.client.HttpClientErrorException e) {
			logger.error("film "+title+" not found");
		}
		return null;
	}
	/**
	 * we're retrieving in Allocine the desk critics
	 * @param code
	 * @return
	 */
	public SearchResults retrieveAllocineReviewFeedByCode(final Integer code,final int page) {
		try {
			return restTemplate.getForObject(environment.getRequiredProperty(ALLOCINE_URL_)+environment.getRequiredProperty(ALLOCINE_QUERY_SEARCH_REVIEW_LIST)+"?"+"partner="+environment.getRequiredProperty(ALLOCINE_QUERY_PARTNER)+"&format=json"+"&filter="+environment.getRequiredProperty(ALLOCINE_QUERY_FILTER_DESK_PRESS)+"&subject=movie:"+code+"&page="+page, SearchResults.class);
		}catch(org.springframework.web.client.HttpClientErrorException | HttpServerErrorException e) {
			logger.error("desk-press "+code+" not found");
		}
		return null;
	}
	private void addSearchResultsToSet(Set<Review> reviewsSet, final SearchResults searchReviewFeedResults) {
		reviewsSet.addAll(searchReviewFeedResults.getFeed().getReview());
	}
	/**
	 * 
	 * @param review
	 * @return
	 */
	private CritiquesPresse transformReviewToCritiquesPresse(final Review review) {
		CritiquesPresse critiquesPresse = new CritiquesPresse();
		critiquesPresse.setAuteur(StringUtils.left(review.getAuthor(), 49));
		critiquesPresse.setCode(review.getCode());
		critiquesPresse.setCritique(review.getBody());
		if(review.getNewsSource() != null && StringUtils.isNotEmpty(review.getNewsSource().getName())) {
			critiquesPresse.setNomSource(review.getNewsSource().getName());
		}
		critiquesPresse.setNote(review.getRating());
		return critiquesPresse;
	}
	public void addCritiquesPresseToFilm(Film film) {
		Set<Review> reviewsSet = null;
		Integer firstPage = Integer.valueOf(1);
		Set<CritiquesPresse> critiquePresseSet = null;
		SearchResults searchMovieResults = retrieveAllocineMovieFeedByTitle(film.getTitre());
		if(searchMovieResults != null && searchMovieResults.getFeed() != null && searchMovieResults.getFeed().getMovie() != null && searchMovieResults.getFeed().getMovie().get(0) != null) {
			int code = searchMovieResults.getFeed().getMovie().get(0).getCode();
			if(code != 0) {
				SearchResults searchReviewFeedResults = retrieveAllocineReviewFeedByCode(code,1);
				if(searchReviewFeedResults != null && searchReviewFeedResults.getFeed() != null && searchReviewFeedResults.getFeed().getReview() != null && searchReviewFeedResults.getFeed().getReview().get(0) != null) {
					reviewsSet = new HashSet<>(searchMovieResults.getFeed().getTotalResults().intValue());
					addSearchResultsToSet(reviewsSet, searchReviewFeedResults);
				}
				double nbPagesd = Math.ceil(searchReviewFeedResults.getFeed().getTotalResults().doubleValue()/searchReviewFeedResults.getFeed().getCount().doubleValue());
				int nbPages = Double.valueOf(nbPagesd).intValue();
				while(firstPage.intValue() < nbPages) {
					firstPage = firstPage + Integer.valueOf(1);
					searchReviewFeedResults = retrieveAllocineReviewFeedByCode(code, firstPage);
					addSearchResultsToSet(reviewsSet, searchReviewFeedResults);
				}
				if(CollectionUtils.isNotEmpty(reviewsSet)) {
					critiquePresseSet = new HashSet<>(reviewsSet.size());
					for(Review review : reviewsSet) {
						CritiquesPresse transformedCritiquesPresse = transformReviewToCritiquesPresse(review);
						if(transformedCritiquesPresse != null) {
							filmService.saveCritiquesPresse(transformedCritiquesPresse);
							critiquePresseSet.add(transformedCritiquesPresse);
						}
					}
					if(CollectionUtils.isNotEmpty(critiquePresseSet)) {
						film.getCritiquesPresse().addAll(critiquePresseSet);
						/*
						film.getCritiquesPresse().stream().sorted(new Comparator<CritiquesPresse>() {
							public int compare(CritiquesPresse o1, CritiquesPresse o2) {
								return o1.getNote().compareTo(o2.getNote());
							};
						});*/
					}
				}
			}
		}
	}
}
