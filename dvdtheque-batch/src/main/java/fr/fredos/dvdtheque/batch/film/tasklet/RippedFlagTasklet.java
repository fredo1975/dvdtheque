package fr.fredos.dvdtheque.batch.film.tasklet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.RippedFilm;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.service.FilmService;

public class RippedFlagTasklet implements Tasklet {
	protected Logger logger = LoggerFactory.getLogger(RippedFlagTasklet.class);

	@Autowired
	protected FilmService filmService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<RippedFilm> list = filmService.findAllRippedFilms();
		for (RippedFilm rippedFilm : list) {
			try {
				FilmDto film = filmService.findFilmByTitre(rippedFilm.getTitre());
				film.setRipped(true);
				Film f = film.fromDto();
				filmService.updateFilm(f);
				logger.debug(film.toString());
			} catch (EmptyResultDataAccessException e) {
				// logger.error(titre+" not found");
			}
		}
		return RepeatStatus.FINISHED;
	}
}
