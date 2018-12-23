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
import fr.fredos.dvdtheque.service.PersonneService;
import fr.fredos.dvdtheque.service.dto.FilmUtils;
import fr.fredos.dvdtheque.web.controller.FilmServiceTest;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class,fr.fredos.dvdtheque.web.controller.WebApplication.class})
public class FilmServiceWebTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	protected FilmService filmService;
	@Autowired
	protected PersonneService personneService;
	private final static String MAX_ID_SQL = "select max(id) from FILM";
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		Film film = null;
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
			if(idRealisateur==null) {
				personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM));
				idRealisateur = this.jdbcTemplate.queryForObject(FilmServiceTest.MAX_PERSONNE_ID_SQL, Integer.class);
			}
			Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
			if(idActeur1==null) {
				personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT2_NOM,FilmUtils.ACT2_PRENOM));
				idActeur1 = this.jdbcTemplate.queryForObject(FilmServiceTest.MAX_PERSONNE_ID_SQL, Integer.class);
			}
			film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
			id = filmService.saveNewFilm(film);
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
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		if(idRealisateur==null) {
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM));
			idRealisateur = this.jdbcTemplate.queryForObject(FilmServiceTest.MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		if(idActeur1==null) {
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT2_NOM,FilmUtils.ACT2_PRENOM));
			idActeur1 = this.jdbcTemplate.queryForObject(FilmServiceTest.MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		Integer id = filmService.saveNewFilm(film);
		assertNotNull(id);
		List<Film> filmList = filmService.findAllFilms();
		assertNotNull(filmList);
		logger.debug(methodName + "filmList ="+filmList.toString());
		logger.debug(methodName + "end");
	}
}
