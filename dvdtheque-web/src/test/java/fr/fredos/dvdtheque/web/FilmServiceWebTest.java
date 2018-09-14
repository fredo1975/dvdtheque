package fr.fredos.dvdtheque.web;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dto.ActeurDto;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.service.FilmService;

@ContextConfiguration(locations={"classpath:business-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class FilmServiceWebTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	protected FilmService filmService;
	private final static String MAX_ID_SQL = "select max(id) from FILM";
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.info(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		FilmDto film = filmService.findFilmWithAllObjectGraph(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.info(methodName + "film ="+film.toString());
		assertNotNull(film.getPersonnesFilm().getActeurs());
		for(ActeurDto acteur : film.getPersonnesFilm().getActeurs()){
			logger.info(methodName + " acteur="+acteur.toString());
		}
		logger.info(methodName + "end");
	}
	
	@Test
	public void findAllFilms() throws Exception{
		String methodName = "findAllFilms : ";
		logger.info(methodName + "start");
		Integer id = new Integer(30);
		List<Film> filmList = filmService.getAllFilms();
		assertNotNull(filmList);
		logger.info(methodName + "filmList ="+filmList.toString());
		logger.info(methodName + "end");
	}
}
