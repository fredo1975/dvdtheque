package fr.fredos.dvdtheque.web.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.dto.FilmUtils;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class,fr.fredos.dvdtheque.web.controller.WebApplication.class})
public class FilmServiceWebTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	protected FilmService filmService;
	private final static String MAX_ID_SQL = "select max(id) from FILM";
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		Film film = null;
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			id = filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
			assertNotNull(id);
		}
		film = filmService.findFilmWithAllObjectGraph(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		
		assertNotNull(film.getActeurs());
		for(Personne acteur : film.getActeurs()){
			logger.info(" acteur="+acteur.toString());
		}
	}
	
	@Test
	public void findAllFilms() throws Exception{
		String methodName = "findAllFilms : ";
		logger.debug(methodName + "start");
		filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
		List<Film> filmList = filmService.findAllFilms();
		assertNotNull(filmList);
		logger.debug(methodName + "filmList ="+filmList.toString());
		logger.debug(methodName + "end");
	}
}
