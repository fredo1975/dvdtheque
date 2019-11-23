package fr.fredos.dvdtheque.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(FilmControllerTest.class);
	@Autowired
	private MockMvc mvc;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
	private TmdbServiceClient client;
	@Autowired
	ExcelFilmHandler excelFilmHandler;
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private static final String GET_ALL_FILMS_URI = "/dvdtheque/films/";
	private static final String GET_CLEAN_ALL_FILMS_URI = "/dvdtheque/films/cleanAllfilms/";
	private static final String GET_ALL_GENRES_URI = "/dvdtheque/films/genres/";
	private static final String UPDATE_PERSONNE_URI = "/dvdtheque/personnes/byId/";
	private static final String SEARCH_PERSONNE_URI = GET_ALL_FILMS_URI+"byPersonne";
	private static final String SEARCH_ALL_REALISATEUR_URI = "/dvdtheque/realisateurs";
	private static final String SEARCH_ALL_REALISATEUR_BY_ORIGINE_URI = SEARCH_ALL_REALISATEUR_URI + "/byOrigine/";
	private static final String SEARCH_ALL_ACTTEUR_URI = "/dvdtheque/acteurs";
	private static final String SEARCH_ALL_ACTTEUR_BY_ORIGINE_URI = SEARCH_ALL_ACTTEUR_URI + "/byOrigine/";
	private static final String SEARCH_FILM_BY_ID = GET_ALL_FILMS_URI+"byId/";
	private static final String SEARCH_FILM_BY_TMDBID = GET_ALL_FILMS_URI+"byTmdbId/";
	private static final String SEARCH_TMDB_FILM_BY_TITRE = GET_ALL_FILMS_URI+"tmdb/byTitre/";
	private static final String SEARCH_FILMS_BY_ORIGINE = GET_ALL_FILMS_URI+"byOrigine/";
	private static final String UPDATE_TMDB_FILM_BY_TMDBID = GET_ALL_FILMS_URI+"tmdb/";
	private static final String SAVE_FILM_URI = GET_ALL_FILMS_URI+"save/";
	private static final String UPDATE_FILM_URI = GET_ALL_FILMS_URI+"update/";
	private static final String REMOVE_FILM_URI = GET_ALL_FILMS_URI+"remove/";
	private static final String SEARCH_ALL_PERSONNE_URI = "/dvdtheque/personnes";
	private static final String EXPORT_FILM_LIST_URI = GET_ALL_FILMS_URI+"export";
	private Long tmdbId1 = new Long(1271);
	private Long tmdbId2 = new Long(844);
	private Long tmdbId3 = new Long(4780);
	private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w500/6ldXqZhCxcnzlgMU70CLPvZI8if.jpg";
	public static final String SHEET_NAME = "Films";
	
	private void assertCacheSize(final int mapActeursByOrigineSize,final int mapRealisateursByOrigineSize,final FilmOrigine filmOrigine) {
		assertEquals(mapActeursByOrigineSize, filmService.findAllActeursByOrigine(filmOrigine).size());
		assertEquals(mapRealisateursByOrigineSize, filmService.findAllRealisateursByOrigine(filmOrigine).size());
	}
	@Before()
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
	}
	@Test
	public void cleanAllFilms() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(GET_CLEAN_ALL_FILMS_URI)
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Test
	public void findAllFilmsByOrigine() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Film> allFilms = filmService.findAllFilms();
		assertNotNull(allFilms);
		if (CollectionUtils.isNotEmpty(allFilms)) {
			assertTrue(allFilms.size()==1);
			Film filmToTest = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
			assertNotNull(filmToTest);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.DVD.name())
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc
					.perform(MockMvcRequestBuilders.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.DVD.name()).contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(filmToTest.getTitre())));
			assertNotNull(resultActions);
			FilmBuilder.assertFilmIsNotNull(filmToTest, true, FilmBuilder.RIP_DATE_OFFSET, true);
		}
	}
	@Test
	public void findAllFilms() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Film> allFilms = filmService.findAllFilms();
		assertNotNull(allFilms);
		if (CollectionUtils.isNotEmpty(allFilms)) {
			assertTrue(allFilms.size()==1);
			Film filmToTest = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
			assertNotNull(filmToTest);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(GET_ALL_FILMS_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc
					.perform(MockMvcRequestBuilders.get(GET_ALL_FILMS_URI).contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(filmToTest.getTitre())));
			assertNotNull(resultActions);
			FilmBuilder.assertFilmIsNotNull(filmToTest, true, FilmBuilder.RIP_DATE_OFFSET, true);
		}
	}
	@Test
	public void findAllGenres() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Genre> allGenres = filmService.findAllGenres();
		assertNotNull(allGenres);
		assertTrue(CollectionUtils.isNotEmpty(allGenres));
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(GET_ALL_GENRES_URI)
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Is.is("Action")));
	}
	@Test
	public void findTmdbFilmByTitre() throws Exception {
		String titre = "BROADWAY OR BUST";
		Film film = new Film();
		film.setTitre(titre);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_TMDB_FILM_BY_TITRE + titre)
				.contentType(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(builder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is("BROADWAY OR BUST")));
		assertNotNull(resultActions);
	}

	@Test
	public void findById() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_FILM_BY_ID + film.getId())
				.contentType(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		assertNotNull(resultActions);
	}

	@Test
	public void findAllRealisateurs() throws Exception {
		filmService.cleanAllFilms();
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Personne> allRealisateur = filmService.findAllRealisateurs();
		assertNotNull(allRealisateur);
		if (CollectionUtils.isNotEmpty(allRealisateur)) {
			Personne realisateur = allRealisateur.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc
					.perform(MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_URI)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(realisateur.getNom())));
			assertNotNull(resultActions);
		}
	}
	@Test
	public void findAllRealisateursByOrigine() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Personne> allActeur = filmService.findAllRealisateursByOrigine(FilmOrigine.DVD);
		assertNotNull(allActeur);
		if (CollectionUtils.isNotEmpty(allActeur)) {
			Personne acteur = allActeur.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_BY_ORIGINE_URI + FilmOrigine.DVD.name())
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc
					.perform(MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_BY_ORIGINE_URI + FilmOrigine.DVD.name()).contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(acteur.getNom())));
			assertNotNull(resultActions);
		}
	}

	@Test
	public void findAllPersonne() throws Exception {
		filmService.cleanAllFilms();
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, true);
		List<Personne> allPersonne = personneService.findAllPersonne();
		assertNotNull(allPersonne);
		if (CollectionUtils.isNotEmpty(allPersonne)) {
			Personne personne = allPersonne.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_PERSONNE_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc
					.perform(
							MockMvcRequestBuilders.get(SEARCH_ALL_PERSONNE_URI).contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(personne.getNom())));
			assertNotNull(resultActions);
		}
	}

	@Test
	public void findAllActeurs() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Personne> allActeur = filmService.findAllActeurs();
		assertNotNull(allActeur);
		if (CollectionUtils.isNotEmpty(allActeur)) {
			Personne acteur = allActeur.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_ACTTEUR_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc
					.perform(MockMvcRequestBuilders.get(SEARCH_ALL_ACTTEUR_URI).contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(acteur.getNom())));
			assertNotNull(resultActions);
		}
	}
	@Test
	public void findAllActeursByOrigine() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Personne> allActeur = filmService.findAllActeursByOrigine(FilmOrigine.DVD);
		assertNotNull(allActeur);
		if (CollectionUtils.isNotEmpty(allActeur)) {
			Personne acteur = allActeur.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_ACTTEUR_BY_ORIGINE_URI + FilmOrigine.DVD.name())
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc
					.perform(MockMvcRequestBuilders.get(SEARCH_ALL_ACTTEUR_BY_ORIGINE_URI + FilmOrigine.DVD.name()).contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(acteur.getNom())));
			assertNotNull(resultActions);
		}
	}

	@Test
	@Transactional
	public void testCheckIfTmdbFilmExists() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_FILM_BY_TMDBID + film.getTmdbId())
				.contentType(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(Boolean.TRUE)));
		assertNotNull(resultActions);

	}

	@Test
	@Transactional
	public void testReplaceFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(film);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(UPDATE_TMDB_FILM_BY_TMDBID + tmdbId1, film)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		Film filmUpdated = filmService.findFilm(film.getId());
		FilmBuilder.assertFilmIsNotNull(filmUpdated, true, FilmBuilder.RIP_DATE_OFFSET, true);
		Results results = client.retrieveTmdbSearchResultsById(tmdbId1);
		assertEquals(StringUtils.upperCase(results.getTitle()), filmUpdated.getTitre());
		assertEquals(POSTER_PATH, filmUpdated.getPosterPath());
	}

	@Test
	@Transactional
	public void testUpdateFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.getDvd().setRipped(false);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_4780)));
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertFalse(filmUpdated.getDvd().isRipped());
		assertCacheSize(0, 0, FilmOrigine.EN_SALLE);
		assertCacheSize(3, 1, FilmOrigine.DVD);
	}
	
	@Test(expected = java.lang.Exception.class)
	@Transactional
	public void testRemoveFilm() throws Exception{
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film filmToRemove = filmService.findFilm(film.getId());
		assertNotNull(filmToRemove);
		logger.debug("filmToRemove=" + filmToRemove.toString());
		
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(filmToRemove);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(REMOVE_FILM_URI + film.getId(), filmToRemove)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmRemoved = filmService.findFilm(filmToRemove.getId());
		assertNull(filmRemoved);
		assertCacheSize(0, 0, FilmOrigine.DVD);
	}
	
	@Test
	@Transactional
	public void testTransfertEnSalleToDvdFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.setOrigine(FilmOrigine.DVD);
		filmToUpdate.setDvd(new Dvd());
		filmToUpdate.getDvd().setAnnee(2019);
		filmToUpdate.getDvd().setEdition("");
		filmToUpdate.getDvd().setFormat(DvdFormat.DVD);
		filmToUpdate.getDvd().setZone(new Integer(1));
		filmToUpdate.getDvd().setRipped(true);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_4780)));
		Film filmUpdated = filmService.findFilm(film.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertEquals(FilmOrigine.DVD, filmUpdated.getOrigine());
		assertEquals(DvdFormat.DVD, filmUpdated.getDvd().getFormat());
		assertEquals(new Integer(1), filmUpdated.getDvd().getZone());
		assertTrue(filmUpdated.getDvd().isRipped());
		
		assertCacheSize(0, 0, FilmOrigine.EN_SALLE);
		assertCacheSize(3, 1, FilmOrigine.DVD);
	}

	@Test
	@Transactional
	@Ignore
	public void testUpdateWithRestSaveFilm() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(SAVE_FILM_URI + tmdbId3)
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		String response = mvc.perform(builder).andReturn().getResponse().getContentAsString();
		logger.debug("response=" + response);
		ObjectMapper mapper = new ObjectMapper();
		Film filmToUpdate = mapper.readValue(response, Film.class);
		logger.debug("filmToUpdate=" + filmToUpdate);
		filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.getDvd().setRipped(true);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		builder = MockMvcRequestBuilders.put(UPDATE_FILM_URI + filmToUpdate.getId(), filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertTrue(filmUpdated.getDvd().isRipped());
	}

	@Test
	@Transactional
	public void testSaveNewFilmDvd() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(SAVE_FILM_URI + tmdbId2)
				.contentType(MediaType.APPLICATION_JSON).content(FilmOrigine.DVD.name());
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		Film filmSaved = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		FilmBuilder.assertFilmIsNotNull(filmSaved, true, FilmBuilder.RIP_DATE_OFFSET, true);
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmSaved.getTitre());
	}
	
	@Test
	@Transactional
	public void testSaveNewFilmEnSalle() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(SAVE_FILM_URI + tmdbId2)
				.contentType(MediaType.APPLICATION_JSON).content(FilmOrigine.EN_SALLE.name());
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		Film filmSaved = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		FilmBuilder.assertFilmIsNotNull(filmSaved, true, 0, false);
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmSaved.getTitre());
		assertCacheSize(12, 1, FilmOrigine.EN_SALLE);
		assertCacheSize(0, 0, FilmOrigine.DVD);
	}

	@Test
	@Transactional
	public void testFindPersonne() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Personne personne = personneService.findPersonneByName(FilmBuilder.REAL_NOM_TMBD_ID_844);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_PERSONNE_URI)
				.param("nom", personne.getNom()).contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.nom", Is.is(personne.getNom())));
	}

	@Test
	@Transactional
	public void testUpdatePersonne() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Personne personne = personneService.findPersonneByName(FilmBuilder.ACT1_TMBD_ID_844);
		assertNotNull(personne);
		personne.setNom(FilmBuilder.ACT2_TMBD_ID_844);
		ObjectMapper mapper = new ObjectMapper();
		String personneJsonString = mapper.writeValueAsString(personne);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put(UPDATE_PERSONNE_URI + personne.getId(), personne).contentType(MediaType.APPLICATION_JSON)
				.content(personneJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Personne personneUpdated = personneService.loadPersonne(personne.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.ACT2_TMBD_ID_844), personneUpdated.getNom());
	}
	
	@Test
	public void testExportFilmList() throws Exception {
		filmService.cleanAllFilms();
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setVu(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setRipped(true)
				.setVu(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.BLUERAY)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2)).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, true);
		FilmBuilder.assertFilmIsNotNull(film2, false, FilmBuilder.RIP_DATE_OFFSET2, true);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(EXPORT_FILM_LIST_URI).content(FilmOrigine.DVD.name());
		
		MvcResult result = mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
		byte[] b = result.getResponse().getContentAsByteArray();
		assertNotNull(b);
		Workbook workbook = excelFilmHandler.createSheetFromByteArray(b);
        workbook.forEach(sheet -> {
        	assertEquals(SHEET_NAME, sheet.getSheetName());
        });
        Sheet sheet = workbook.getSheetAt(0);
        assertEquals(SHEET_NAME, sheet.getSheetName());
        DataFormatter dataFormatter = new DataFormatter();
        sheet.forEach(row -> {
        	if(row.getRowNum()==2) {
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_4780, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), StringUtils.upperCase(cellValue));
                    }
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertEquals(FilmBuilder.ACT3_TMBD_ID_4780 + ","+FilmBuilder.ACT1_TMBD_ID_4780 + "," + FilmBuilder.ACT2_TMBD_ID_4780, cellValue);
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==7) {
                    	assertEquals(FilmBuilder.ZONE_DVD, cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals(DvdFormat.BLUERAY.name(), cellValue);
                    }
                });
        	}else if(row.getRowNum()==1){
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_844, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), StringUtils.upperCase(cellValue));
                    }
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertEquals(FilmBuilder.ACT1_TMBD_ID_844 + "," + FilmBuilder.ACT2_TMBD_ID_844 + "," + FilmBuilder.ACT3_TMBD_ID_844, cellValue);
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==7) {
                    	assertEquals(FilmBuilder.ZONE_DVD, cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals(DvdFormat.DVD.name(), cellValue);
                    }
                });
        	}
        });
	}
}
