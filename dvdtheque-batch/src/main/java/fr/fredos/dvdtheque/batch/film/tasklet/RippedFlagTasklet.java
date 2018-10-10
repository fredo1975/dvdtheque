package fr.fredos.dvdtheque.batch.film.tasklet;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.Assert;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.dto.FilmDto;

public class RippedFlagTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(RippedFlagTasklet.class);
	private Resource directory;
	@Autowired
	protected FilmService filmService;
	/*@Value( "${file.extension}" )
	private String fileExtension;*/
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		File dir = directory.getFile();
        Assert.state(dir.isDirectory());

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
        	String name = files[i].getName();
        	String extension = StringUtils.substringAfter(name, ".");
        	if(extension.equalsIgnoreCase("mkv")) {
        		String titre = StringUtils.substringBefore(name, ".");
        		try {
        			FilmDto film = filmService.findFilmByTitre(titre);
        			film.setRipped(true);
        			Film f = film.fromDto();
        			filmService.updateFilm(f);
        			logger.debug(film.toString());
        		}catch(EmptyResultDataAccessException e) {
        			//logger.error(titre+" not found");
        		}
        	}
        }
		return RepeatStatus.FINISHED;
	}
	
	public void setDirectoryResource(Resource directory) {
        this.directory = directory;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(directory, "directory must be set");
    }
}
