package fr.fredos.dvdtheque.swing.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.Is;
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
	@Autowired
	private MockMvc mvc;
	private static final String GET_ALL_FILMS_URI = "/dvdtheque/films/";
	
	@Test
	//@Ignore
	public void findAllFilmsWithControllerDependency() throws Exception {
		List<Film> allFilms = filmService.findAllFilms();
		assertNotNull(allFilms);
		if(CollectionUtils.isNotEmpty(allFilms)) {
			assertTrue(allFilms.size()>0);
			Film filmToTest = allFilms.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(GET_ALL_FILMS_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(GET_ALL_FILMS_URI)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(filmToTest.getTitre())));
			assertNotNull(resultActions);
			assertNotNull(filmToTest);
		}
	}
	
	@Test
	//@Ignore
	public void findAllFilmsRestService() throws Exception {
		List<Film> filmList = filmRestService.findAllFilms();
		assertNotNull(filmList);
		assertTrue(CollectionUtils.isNotEmpty(filmList));
	}
	
	@Test
	//@Ignore
	public void findTmdbFilmByTitre() throws Exception {
		final String titre = "camping";
		Set<Film> filmSet = filmRestService.findTmdbFilmByTitre(titre);
		assertNotNull(filmSet);
		assertTrue(CollectionUtils.isNotEmpty(filmSet));
	}
}
