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
import org.springframework.util.Assert;

import fr.fredos.dvdtheque.dao.model.object.RippedFilm;
import fr.fredos.dvdtheque.service.FilmService;

public class SaveRippedFilmTasklet implements Tasklet{
	protected Logger logger = LoggerFactory.getLogger(SaveRippedFilmTasklet.class);
	private Resource directory;
	@Autowired
	protected FilmService filmService;
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		File dir = directory.getFile();
        Assert.state(dir.isDirectory());

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
        	String name = files[i].getName();
        	String extension = StringUtils.substringAfter(name, ".");
        	if(extension.equalsIgnoreCase("mkv")) {
        		filmService.saveNewRippedFilm(new RippedFilm(StringUtils.substringBefore(name, ".")));
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
