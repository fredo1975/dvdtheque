package fr.fredos.dvdtheque.batch.film.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;
@Component(value="cleanDBTasklet")
public class CleanDBTasklet implements Tasklet{
	@Autowired
	protected PersonneService personneService;
	@Autowired
	protected FilmService filmService;
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		personneService.cleanAllPersonnes();
		filmService.cleanAllFilms();
		return RepeatStatus.FINISHED;
	}

}
