package fr.fredos.dvdtheque.batch.film.tasklet;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
@Component(value="rippedFlagTasklet")
public class RippedFlagTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(RippedFlagTasklet.class);
	private static String LISTE_DVD_FILE_PATH="dvd.file.path";
	private static String RIPPEDFLAGTASKLET_FROM_FILE="rippedFlagTasklet.from.file";
	@Autowired
	protected IFilmService filmService;
	@Autowired
    Environment environment;
	
	/*@Value( "${file.extension}" )
	private String fileExtension;*/
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		boolean loadFromFile = Boolean.valueOf(environment.getRequiredProperty(RIPPEDFLAGTASKLET_FROM_FILE));
		if(!loadFromFile) {
			Resource directory = new FileSystemResource(environment.getRequiredProperty(LISTE_DVD_FILE_PATH));
			File dir = directory.getFile();
			Assert.notNull(directory, "directory must be set");
	        File[] files = dir.listFiles();
	        for (int i = 0; i < files.length; i++) {
	        	String name = files[i].getName();
	        	long millis = files[i].lastModified();
	        	Calendar cal = Calendar.getInstance(Locale.FRANCE);
	        	cal.setTimeInMillis(millis);
	        	String extension = StringUtils.substringAfter(name, ".");
	        	if(extension.equalsIgnoreCase("mkv")) {
	        		String titre = StringUtils.substringBefore(name, ".");
	        		try {
	        			Film film = filmService.findFilmByTitre(titre);
	        			if(film != null) {
	        				film.setRipped(true);
	        				film.getDvd().setDateRip(cal.getTime());
	            			filmService.updateFilm(film);
	            			logger.debug(film.toString());
	        			}
	        		}catch(EmptyResultDataAccessException e) {
	        			//logger.error(titre+" not found");
	        		}
	        	}
	        }
		}else {
			logger.info("nothing to do");
		}
		return RepeatStatus.FINISHED;
	}
	
}
