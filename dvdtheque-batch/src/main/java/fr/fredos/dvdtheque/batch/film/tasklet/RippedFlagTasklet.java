package fr.fredos.dvdtheque.batch.film.tasklet;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;
@Component(value="rippedFlagTasklet")
public class RippedFlagTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(RippedFlagTasklet.class);
	private static String LISTE_DVD_FILE_PATH="dvd.file.path";
	@Autowired
	protected FilmService filmService;
	@Autowired
    Environment environment;
	
	/*@Value( "${file.extension}" )
	private String fileExtension;*/
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Resource directory = new FileSystemResource(environment.getRequiredProperty(LISTE_DVD_FILE_PATH));
		File dir = directory.getFile();
		Assert.notNull(directory, "directory must be set");

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
        	String name = files[i].getName();
        	String extension = StringUtils.substringAfter(name, ".");
        	if(extension.equalsIgnoreCase("mkv")) {
        		String titre = StringUtils.substringBefore(name, ".");
        		try {
        			Film film = filmService.findFilmByTitre(titre);
        			film.setRipped(true);
        			filmService.updateFilm(film);
        			logger.debug(film.toString());
        		}catch(EmptyResultDataAccessException e) {
        			//logger.error(titre+" not found");
        		}
        	}
        }
		return RepeatStatus.FINISHED;
	}
	
}
