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
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.dto.ActeurDto;
import fr.fredos.dvdtheque.service.dto.FilmDto;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class,fr.fredos.dvdtheque.web.controller.WebApplication.class})
public class FilmServiceWebTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	protected FilmService filmService;
	private final static String MAX_ID_SQL = "select max(id) from FILM";
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto("titre"));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilmWithAllObjectGraph(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug(methodName + "film ="+film.toString());
		assertNotNull(film.getPersonnesFilm().getActeurs());
		for(ActeurDto acteur : film.getPersonnesFilm().getActeurs()){
			logger.info(methodName + " acteur="+acteur.toString());
		}
		logger.debug(methodName + "end");
	}
	
	@Test
	public void findAllFilms() throws Exception{
		String methodName = "findAllFilms : ";
		logger.debug(methodName + "start");
		FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
		assertNotNull(film);
		List<Film> filmList = filmService.getAllFilms();
		assertNotNull(filmList);
		logger.debug(methodName + "filmList ="+filmList.toString());
		logger.debug(methodName + "end");
	}
}
