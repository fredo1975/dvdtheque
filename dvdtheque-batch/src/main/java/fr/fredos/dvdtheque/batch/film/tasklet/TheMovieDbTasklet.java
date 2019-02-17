package fr.fredos.dvdtheque.batch.film.tasklet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.impl.FilmServiceImpl;
import fr.fredos.dvdtheque.tmdb.model.ImagesResults;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@Component(value="theMovieDbTasklet")
public class TheMovieDbTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(TheMovieDbTasklet.class);
	@Autowired
	protected FilmService filmService;
	@Autowired
    Environment environment;
	@Autowired
    private TmdbServiceClient tmdbServiceClient;
	private static String LISTE_DVD_POSTER_FILE_PATH="dvd.poster.file.path";
	
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
					ImagesResults imagesResults = tmdbServiceClient.retrieveTmdbImagesResults(searchResults.getResults().get(0).getId());
					if(CollectionUtils.isNotEmpty(imagesResults.getPosters())) {
						String imageUrl = tmdbServiceClient.retrieveTmdbFrPosterPathUrl(imagesResults);
					    Resource directory = new FileSystemResource(environment.getRequiredProperty(LISTE_DVD_POSTER_FILE_PATH));
					    String destinationFile = directory.getFile().getAbsolutePath()+"/"+StringUtils.replace(film.getTitre(), " ", "_")+".jpg";
						Assert.notNull(directory, "directory must be set");
						Path path = Paths.get(destinationFile);
						if (Files.notExists(path)) {
							FilmServiceImpl.saveImage(imageUrl, destinationFile);
						}
					}
				}
			} catch (RestClientException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return RepeatStatus.FINISHED;
	}
	
}
