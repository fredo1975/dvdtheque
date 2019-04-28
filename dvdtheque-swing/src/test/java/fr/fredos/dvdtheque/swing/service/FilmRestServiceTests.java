package fr.fredos.dvdtheque.swing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;

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
	private Long tmdbId= new Long(4780);
	public static final String TITRE_FILM_TMBD_ID_4780 = "OBSESSION";
	@Test
	public void findAllFilmsRestService() throws Exception {
		List<Film> filmList = filmRestService.findAllFilms();
		assertNotNull(filmList);
		assertTrue(CollectionUtils.isNotEmpty(filmList));
	}
	
	@Test
	public void findTmdbFilmByTitre() throws Exception {
		final String titre = "camping";
		Set<Film> filmSet = filmRestService.findTmdbFilmByTitre(titre);
		assertNotNull(filmSet);
		assertTrue(CollectionUtils.isNotEmpty(filmSet));
	}
	
	@Test
	public void testAddTmdbFilm() throws Exception {
		Film filmSaved = filmRestService.saveTmdbFilm(tmdbId);
		assertNotNull(filmSaved);
		assertEquals(StringUtils.upperCase(TITRE_FILM_TMBD_ID_4780),filmSaved.getTitre());
	}
}
