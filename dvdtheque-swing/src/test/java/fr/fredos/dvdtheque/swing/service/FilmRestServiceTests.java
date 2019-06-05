package fr.fredos.dvdtheque.swing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class,
		fr.fredos.dvdtheque.swing.service.FilmRestService.class})
@AutoConfigureMockMvc
@Ignore
public class FilmRestServiceTests extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(FilmRestServiceTests.class);
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
	protected FilmRestService filmRestService;
	
	private Long tmdbId;
	public static final String TITRE_FILM_TMBD_ID_4780 = "OBSESSION";
	public static final String TITRE_FILM_UPDATED_TMBD_ID_4780 = "OBSESSION UPDATED";
	private Film filmSaved;
	@Before()
	public void setUp() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		findTmdbFilmToInsert();
	}
	
	private void findTmdbFilmToInsert() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException{
		boolean found = false;
		while(!found) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.tmdbId = ThreadLocalRandom.current().nextLong(200, 479);
			if(!filmRestService.checkIfTmdbFilmExists(this.tmdbId)) {
				found = true;
			}
		}
		this.filmSaved = filmRestService.saveTmdbFilm(this.tmdbId);
	}
	
	@Test
	public void findAllFilmsRestService() throws Exception {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Film> filmList = filmRestService.findAllFilms();
		assertNotNull(filmList);
		assertTrue(CollectionUtils.isNotEmpty(filmList));
	}
	
	@Test
	public void findTmdbFilmByTitre() throws Exception {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Set<Film> filmSet = filmRestService.findTmdbFilmByTitre(this.filmSaved.getTitre());
		assertNotNull(filmSet);
		assertTrue(CollectionUtils.isNotEmpty(filmSet));
	}
	
	@Test
	public void testAddTmdbFilm() throws Exception {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Film film = filmRestService.findFilmById(this.filmSaved.getId());
		assertEquals(StringUtils.upperCase(film.getTitre()),this.filmSaved.getTitre());
		assertFalse(film.isRipped());
	}
	@Test
	public void testUpdateFilm() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Film f = filmRestService.findFilmById(this.filmSaved.getId());
		assertNotNull(f);
		//filmSaved.setTitre(TITRE_FILM_UPDATED_TMBD_ID_4780);
		f.setRipped(true);
		filmRestService.updateFilm(f);
		Film filmUpdated = filmRestService.findFilmById(f.getId());
		assertNotNull(filmUpdated);
		assertTrue(filmUpdated.isRipped());
	}
	@Test
	public void testCheckIfTmdbFilmExists() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Boolean exists = filmRestService.checkIfTmdbFilmExists(this.filmSaved.getTmdbId());
		assertTrue(exists);
	}
}
