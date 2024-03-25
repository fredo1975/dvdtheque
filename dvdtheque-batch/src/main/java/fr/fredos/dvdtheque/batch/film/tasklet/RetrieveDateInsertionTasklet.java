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

@Component(value="retrieveDateInsertionTasklet")
public class RetrieveDateInsertionTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(RetrieveDateInsertionTasklet.class);
	private static String LISTE_DVD_FILE_PATH="dvd.file.path";
	private static String RETRIEVE_DATE_INSERTION="retrieve.date.insertion";
	@Autowired
    Environment environment;
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		/*
		boolean execute = Boolean.valueOf(environment.getRequiredProperty(RETRIEVE_DATE_INSERTION));
		if(execute) {
			Resource directory = new FileSystemResource(environment.getRequiredProperty(LISTE_DVD_FILE_PATH));
			File dir = directory.getFile();
			Assert.notNull(directory, "directory must be set");
	        File[] files = dir.listFiles();
	        for (int i = 0; i < files.length; i++) {
	        	String name = files[i].getName();
	        	logger.debug("name="+name);
	        	long millis = files[i].lastModified();
	        	Calendar cal = Calendar.getInstance(Locale.FRANCE);
	        	cal.setTimeInMillis(millis);
	        	String extension = StringUtils.substringAfter(name, ".");
	        	//if(extension.equalsIgnoreCase("mkv")) {
	        		String titre = StringUtils.substringBefore(name, ".");
	        		try {
	        			Film film = filmService.findFilmByTitreWithoutSpecialsCharacters(titre);
	        			if(film != null) {
	        				Date dateInsertion = DateUtils.clearDate(new Date(millis));
	        				film.setDateInsertion(dateInsertion);
	            			Film mergedFilm = filmService.updateFilm(film);
	            			logger.debug(mergedFilm.toString());
	        				//logger.debug("film="+film.toString());
	        			}else {
	        				
	        			}
	        		}catch(EmptyResultDataAccessException e) {
	        			//logger.error(titre+" not found");
	        		}
	        	//}
	        }
		}else {
			logger.info("nothing to do");
		}
		*/
		return RepeatStatus.FINISHED;
	}

}
