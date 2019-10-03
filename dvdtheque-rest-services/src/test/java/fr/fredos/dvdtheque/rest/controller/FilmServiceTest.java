package fr.fredos.dvdtheque.rest.controller;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilmServiceTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(FilmServiceTest.class);
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
		assertTrue(CollectionUtils.isNotEmpty(film.getGenres()));
		assertTrue(film.getGenres().size() == 2);
		assertEquals(filmService.clearDate(createRipDate()),film.getDvd().getDateRip());
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()==3);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
	}
	@Test
	public void findAllFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(), DvdFormat.DVD, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		film = filmService.findFilmByTitre(TITRE_FILM);
		assertNotNull(film);
		StopWatch watch = new StopWatch();
		logger.debug(watch.prettyPrint());
		watch.start();
		List<Film> films = filmService.findAllFilms();
		assertNotNull(films);
		watch.stop();
		logger.debug(watch.prettyPrint());
	}
}
