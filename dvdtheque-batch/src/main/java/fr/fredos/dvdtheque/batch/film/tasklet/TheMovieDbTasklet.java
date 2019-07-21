package fr.fredos.dvdtheque.batch.film.tasklet;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.tmdb.model.ImagesResults;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@Component(value="theMovieDbTasklet")
public class TheMovieDbTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(TheMovieDbTasklet.class);
	@Autowired
	protected IFilmService filmService;
	@Autowired
    Environment environment;
	@Autowired
    private TmdbServiceClient tmdbServiceClient;
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<Film> filmList = filmService.findAllFilms();
		filmList.forEach(film->{
			logger.info(film.toString());
			SearchResults searchResults = null;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				searchResults = tmdbServiceClient.retrieveTmdbSearchResults(film.getTitre());
				if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
					Results res = tmdbServiceClient.filterSearchResultsByDateRelease(film.getAnnee(), searchResults.getResults());
					ImagesResults imagesResults = tmdbServiceClient.retrieveTmdbImagesResults(res.getId());
					if(CollectionUtils.isNotEmpty(imagesResults.getPosters())) {
						String imageUrl = tmdbServiceClient.retrieveTmdbFrPosterPathUrl(imagesResults);
						film.setPosterPath(imageUrl);
						filmService.updateFilm(film);
					}
				}
			} catch (RestClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return RepeatStatus.FINISHED;
	}
}
