package fr.fredos.dvdtheque.batch.film.tasklet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.film.tmdb.Results;
import fr.fredos.dvdtheque.batch.film.tmdb.SearchResults;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;

@Component(value="theMovieDbTasklet")
public class TheMovieDbTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(TheMovieDbTasklet.class);
	@Autowired
	protected FilmService filmService;
	@Autowired
    Environment environment;
	@Autowired
	RestTemplate restTemplate;
	private static String LISTE_DVD_POSTER_FILE_PATH="dvd.poster.file.path";
	private static String TMDB_SEARCH_MOVIE_QUERY="themoviedb.search.movie.query";
	private static String TMDB_API_KEY="themoviedb.api.key";
	private static String TMDB_POSTER_PATH="themoviedb.poster.path";
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<Film> filmList = filmService.findAllFilms();
		filmList.forEach(film->{
			logger.info(film.toString());
			SearchResults searchResults;
			try {
				searchResults = restTemplate.getForObject(environment.getRequiredProperty(TMDB_SEARCH_MOVIE_QUERY)+"?"+"api_key="+environment.getRequiredProperty(TMDB_API_KEY)+"&query="+film.getTitre(), SearchResults.class);
				logger.info(searchResults.toString());
				Thread.sleep(500);
				if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
					Results results = searchResults.getResults().get(0);
					String imageUrl = environment.getRequiredProperty(TMDB_POSTER_PATH)+results.getPoster_path();
				    Resource directory = new FileSystemResource(environment.getRequiredProperty(LISTE_DVD_POSTER_FILE_PATH));
				    String destinationFile = directory.getFile().getAbsolutePath()+"/"+StringUtils.replace(film.getTitre(), " ", "_")+".jpg";
					Assert.notNull(directory, "directory must be set");
					Path path = Paths.get(destinationFile);
					if (Files.notExists(path)) {
						saveImage(imageUrl, destinationFile);
					}
				}
			} catch (RestClientException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return RepeatStatus.FINISHED;
	}
	
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
	    URL url = new URL(imageUrl);
	    InputStream is = url.openStream();
	    OutputStream os = new FileOutputStream(destinationFile);

	    byte[] b = new byte[2048];
	    int length;

	    while ((length = is.read(b)) != -1) {
	        os.write(b, 0, length);
	    }

	    is.close();
	    os.close();
	}
}
