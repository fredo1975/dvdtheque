package fr.fredos.dvdtheque.web.controller;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;
import fr.fredos.dvdtheque.service.dto.FilmUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class,fr.fredos.dvdtheque.web.controller.WebApplication.class})
public class FilmServiceTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(FilmServiceTest.class);
	@Autowired
	protected FilmService filmService;
	@Autowired
	protected PersonneService personneService;
	public final static String MAX_PERSONNE_ID_SQL = "select max(id) from PERSONNE";
	
	@Test
	public void findAllFilm() throws Exception {
		String methodName = "findAllFilm : ";
		logger.debug(methodName + "start");
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		if(idRealisateur==null) {
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM));
			idRealisateur = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		if(idActeur1==null) {
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT2_NOM));
			idActeur1 = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		filmService.saveNewFilm(film);
		film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(film);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = filmService.findAllFilms();
		assertNotNull(films);
		watch.stop();
		logger.debug(watch.prettyPrint());
		logger.debug(methodName + "end");
	}
}
