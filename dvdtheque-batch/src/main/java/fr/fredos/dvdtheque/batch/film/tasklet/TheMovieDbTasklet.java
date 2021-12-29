package fr.fredos.dvdtheque.batch.film.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value="theMovieDbTasklet")
public class TheMovieDbTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(TheMovieDbTasklet.class);
	@Autowired
    Environment environment;
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		/*
		List<Film> filmList = filmService.findAllFilms(null);
		filmList.forEach(film->{
			logger.info(film.toString());
			SearchResults searchResults = null;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				logger.error(e1.getMessage());
			}
			try {
				searchResults = tmdbServiceClient.retrieveTmdbSearchResults(film.getTitre(), null);
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
		*/
		return RepeatStatus.FINISHED;
	}
}
