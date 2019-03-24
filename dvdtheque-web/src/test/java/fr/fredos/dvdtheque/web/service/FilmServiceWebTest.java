package fr.fredos.dvdtheque.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class,fr.fredos.dvdtheque.web.controller.WebApplication.class})
public class FilmServiceWebTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	public static final String TITRE_FILM = "Lorem Ipsum";
	public static final String TITRE_FILM_UPDATED = "Lorem Ipsum updated";
	public static final Integer ANNEE = 2015;
	public static final String REAL_NOM = "toto titi";
	public static final String REAL_NOM1 = "Dan VanHarp";
	public static final String ACT1_NOM = "tata tutu";
	public static final String ACT2_NOM = "toitoi tuitui";
	public static final String ACT3_NOM = "tuotuo tmitmi";
	public static final String ACT4_NOM = "Graham Collins";
	public static final int RIP_DATE = -10;
	private Date createRipDate() {
		Calendar cal = Calendar.getInstance();
		return DateUtils.addDays(cal.getTime(), RIP_DATE);
	}
	private void assertFilmIsNotNull(Film film) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		assertEquals(filmService.clearDate(createRipDate()),film.getDvd().getDateRip());
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()==3);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
	}
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate());
		assertFilmIsNotNull(film);
		film = filmService.findFilmWithAllObjectGraph(film.getId());
		assertNotNull(film);
		assertNotNull(film.getTitre());
		
		assertNotNull(film.getActeurs());
		for(Personne acteur : film.getActeurs()){
			logger.info(" acteur="+acteur.toString());
		}
	}
	
	@Test
	public void findAllFilms() throws Exception{
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate());
		assertFilmIsNotNull(film);
		List<Film> filmList = filmService.findAllFilms();
		assertNotNull(filmList);
		logger.debug("filmList ="+filmList.toString());
	}
}
