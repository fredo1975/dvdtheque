package fr.fredos.dvdtheque.swing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
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

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class,
		fr.fredos.dvdtheque.swing.service.FilmRestService.class})
@AutoConfigureMockMvc
public class FilmRestServiceTests extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(FilmRestServiceTests.class);
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
	protected FilmRestService filmRestService;
	private Long tmdbId1 = new Long(612152);
	private Long tmdbId2 = new Long(22);
	private Long tmdbId3 = new Long(21);
	private Long tmdbId4 = new Long(20);
	private Long tmdbId5 = new Long(24);
	public static final String TITRE_FILM_TMBD_ID3 = "The Endless Summer";
	@Before()
	public void setUp() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		filmRestService.cleanAllFilms();
	}
	
	private void assertFilmIsNotNull(Film film) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		assertTrue(CollectionUtils.isNotEmpty(film.getGenres()));
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(DvdFormat.DVD.equals(film.getDvd().getFormat()));
	}
	@Test
	public void findAllFilmsRestService() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		filmRestService.cleanAllFilms();
		Film film = filmRestService.saveTmdbFilm(tmdbId1, FilmOrigine.DVD.name());
		assertFilmIsNotNull(film);
		List<Film> filmList = filmRestService.findAllFilms();
		assertNotNull(filmList);
		assertTrue(CollectionUtils.isNotEmpty(filmList));
	}
	
	@Test
	public void findTmdbFilmByTitre() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		Film film = filmRestService.saveTmdbFilm(tmdbId2, FilmOrigine.DVD.name());
		assertFilmIsNotNull(film);
		Set<Film> filmSet = filmRestService.findTmdbFilmByTitre(film.getTitre());
		assertNotNull(filmSet);
		assertTrue(CollectionUtils.isNotEmpty(filmSet));
	}
	
	@Test
	public void testAddTmdbFilm() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException{
		Film film = filmRestService.saveTmdbFilm(tmdbId3, FilmOrigine.DVD.name());
		assertFilmIsNotNull(film);
		Film savedFilm = filmRestService.findFilmById(film.getId());
		assertEquals(StringUtils.upperCase(film.getTitre()),savedFilm.getTitre());
		assertFalse(film.getDvd().isRipped());
	}
	@Test
	public void testUpdateFilm() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		Film film = filmRestService.saveTmdbFilm(tmdbId4, FilmOrigine.DVD.name());
		assertFilmIsNotNull(film);
		Film f = filmRestService.findFilmById(film.getId());
		assertNotNull(f);
		//filmSaved.setTitre(TITRE_FILM_UPDATED_TMBD_ID_4780);
		f.getDvd().setRipped(true);
		filmRestService.updateFilm(f);
		Film filmUpdated = filmRestService.findFilmById(f.getId());
		assertNotNull(filmUpdated);
		assertTrue(filmUpdated.getDvd().isRipped());
	}
	@Test
	public void testCheckIfTmdbFilmExists() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		Film film = filmRestService.saveTmdbFilm(tmdbId5, FilmOrigine.DVD.name());
		assertFilmIsNotNull(film);
		Boolean exists = filmRestService.checkIfTmdbFilmExists(tmdbId5);
		assertTrue(exists);
	}
}
