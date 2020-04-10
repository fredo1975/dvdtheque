package fr.fredos.dvdtheque.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmDisplayType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;
import fr.fredos.dvdtheque.service.model.FilmListParam;
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
	private ObjectMapper mapper;
	@Autowired
	ExcelFilmHandler excelFilmHandler;
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private static final String GET_ALL_FILMS_URI = "/dvdtheque/films/";
	private static final String GET_CLEAN_ALL_FILMS_URI = "/dvdtheque/films/cleanAllfilms/";
	private static final String GET_ALL_GENRES_URI = "/dvdtheque/films/genres/";
	private static final String UPDATE_PERSONNE_URI = "/dvdtheque/personnes/byId/";
	private static final String SEARCH_PERSONNE_URI = GET_ALL_FILMS_URI + "byPersonne";
	private static final String SEARCH_ALL_REALISATEUR_URI = "/dvdtheque/realisateurs";
	private static final String SEARCH_ALL_REALISATEUR_BY_ORIGINE_URI = SEARCH_ALL_REALISATEUR_URI + "/byOrigine/";
	private static final String SEARCH_ALL_ACTTEUR_URI = "/dvdtheque/acteurs";
	private static final String SEARCH_ALL_ACTTEUR_BY_ORIGINE_URI = SEARCH_ALL_ACTTEUR_URI + "/byOrigine/";
	private static final String SEARCH_FILM_BY_ID = GET_ALL_FILMS_URI + "byId/";
	private static final String SEARCH_FILM_BY_TMDBID = GET_ALL_FILMS_URI + "byTmdbId/";
	private static final String SEARCH_TMDB_FILM_BY_TITRE = GET_ALL_FILMS_URI + "tmdb/byTitre/";
	private static final String SEARCH_FILMS_BY_FILM_LIST_PARAM = "/dvdtheque/filmListParam/byOrigine/";
	private static final String SEARCH_FILMS_BY_ORIGINE = GET_ALL_FILMS_URI + "byOrigine/";
	private static final String UPDATE_TMDB_FILM_BY_TMDBID = GET_ALL_FILMS_URI + "tmdb/";
	private static final String SAVE_FILM_URI = GET_ALL_FILMS_URI + "save/";
	private static final String UPDATE_FILM_URI = GET_ALL_FILMS_URI + "update/";
	private static final String REMOVE_FILM_URI = GET_ALL_FILMS_URI + "remove/";
	private static final String SEARCH_ALL_PERSONNE_URI = "/dvdtheque/personnes";
	private static final String EXPORT_FILM_LIST_URI = GET_ALL_FILMS_URI + "export";

	private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w500/6ldXqZhCxcnzlgMU70CLPvZI8if.jpg";
	public static final String SHEET_NAME = "Films";


	@Before()
	public void setUp() throws Exception {
		filmService.cleanAllFilms();
	}

	@Test
	public void cleanAllFilms() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put(GET_CLEAN_ALL_FILMS_URI)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void findAllFilmsByOrigine() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_1271)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_1271)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_1271)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(new Integer(2))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId3);
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(new Integer(2))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId4 = filmService.saveNewFilm(film4);
		FilmBuilder.assertFilmIsNotNull(film4, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId4);
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.TV)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.build();
		Long filmId5 = filmService.saveNewFilm(film5);
		FilmBuilder.assertFilmIsNotNull(film5, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.TV, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId5);
		
		List<Film> dvdFilms = filmService.findAllFilmsByFilmDisplayType(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.DVD));
		assertNotNull("dvdFilms size should be 2", dvdFilms);
		assertTrue("dvdFilms size should be 2", dvdFilms.size() == 2);
		Film _dvdFilm1 = dvdFilms.get(0);
		Film _dvdFilm2 = dvdFilms.get(1);
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.DVD.name()).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(_dvdFilm1.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].titre", Is.is(_dvdFilm2.getTitre())));
		
		List<Film> enSalleFilms = filmService.findAllFilmsByFilmDisplayType(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.EN_SALLE));
		assertNotNull("enSalleFilms size should be 2", dvdFilms);
		assertTrue("enSalleFilms size should be 2", enSalleFilms.size() == 2);
		Film _enSalleFilmsfilm1 = enSalleFilms.get(0);
		Film _enSalleFilmsfilm2 = enSalleFilms.get(1);
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.EN_SALLE.name()).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(_enSalleFilmsfilm1.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].titre", Is.is(_enSalleFilmsfilm2.getTitre())));
		
		List<Film> tvFilms = filmService.findAllFilmsByFilmDisplayType(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.TV));
		assertNotNull("tvFilms size should be 1", tvFilms);
		assertTrue("tvFilms size should be 1", tvFilms.size() == 1);
		Film _tvFilm1 = tvFilms.get(0);
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.TV.name()).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(_tvFilm1.getTitre())));
		
		
		List<Film> allFilms = filmService.findAllFilmsByFilmDisplayType(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.TOUS));
		assertNotNull("allFilms size should be 5", allFilms);
		assertTrue("allFilms size should be 5", allFilms.size() == 5);
		Film _film1 = allFilms.get(0);
		Film _film2 = allFilms.get(1);
		Film _film3 = allFilms.get(2);
		Film _film4 = allFilms.get(3);
		Film _film5 = allFilms.get(4);
		
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.TOUS.name()).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(_film1.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].titre", Is.is(_film2.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].titre", Is.is(_film3.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[3].titre", Is.is(_film4.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[4].titre", Is.is(_film5.getTitre())));
	}

	@Test
	public void findAllLastAddedFilmsByOrigine() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		final String dateInsertion1 = "2014/08/01";
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(dateInsertion1)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		final String dateInsertion2 = "2014/09/01";
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(dateInsertion2)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId2);
		final String dateInsertion3 = "2014/10/01";
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_1271)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_1271)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_1271)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(dateInsertion3)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(new Integer(2))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId3);
		final String dateInsertion4 = "2014/11/01";
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(dateInsertion4)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(new Integer(2))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId4 = filmService.saveNewFilm(film4);
		FilmBuilder.assertFilmIsNotNull(film4, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId4);
		final String dateInsertion5 = "2014/12/01";
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(dateInsertion5)
				.setOrigine(FilmOrigine.TV)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.build();
		Long filmId5 = filmService.saveNewFilm(film5);
		FilmBuilder.assertFilmIsNotNull(film5, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.TV, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId5);
		
		List<Film> dvdFilms = filmService.findAllFilmsByFilmDisplayType(new FilmDisplayTypeParam(FilmDisplayType.DERNIERS_AJOUTS,1,FilmOrigine.DVD));
		assertNotNull("dvdFilms size should be 1", dvdFilms);
		assertTrue("dvdFilms size should be 1", dvdFilms.size() == 1);
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.DVD.name()).param("displayType", FilmDisplayType.DERNIERS_AJOUTS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film2.getTitre())));
		
		List<Film> enSalleFilms = filmService.findAllFilmsByFilmDisplayType(new FilmDisplayTypeParam(FilmDisplayType.DERNIERS_AJOUTS,1,FilmOrigine.EN_SALLE));
		assertNotNull("enSalleFilms size should be 1", dvdFilms);
		assertTrue("enSalleFilms size should be 1", enSalleFilms.size() == 1);
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.EN_SALLE.name()).param("displayType", FilmDisplayType.DERNIERS_AJOUTS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film4.getTitre())));
		
		List<Film> tvFilms = filmService.findAllFilmsByFilmDisplayType(new FilmDisplayTypeParam(FilmDisplayType.DERNIERS_AJOUTS,1,FilmOrigine.TV));
		assertNotNull("tvFilms size should be 1", tvFilms);
		assertTrue("tvFilms size should be 1", tvFilms.size() == 1);
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.TV.name()).param("displayType", FilmDisplayType.DERNIERS_AJOUTS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film5.getTitre())));
		
		mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.TOUS.name()).param("displayType", FilmDisplayType.DERNIERS_AJOUTS.name())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film5.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].titre", Is.is(film4.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].titre", Is.is(film3.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[3].titre", Is.is(film2.getTitre())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[4].titre", Is.is(film.getTitre())));
	}

	@Test
	public void findAllFilms() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		
		Film filmToTest = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		assertNotNull(filmToTest);
		ResultActions resultActions = mvc
				.perform(MockMvcRequestBuilders.get(GET_ALL_FILMS_URI).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film.getTitre())));
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
		FilmBuilder.assertFilmIsNotNull(filmToTest, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null);
	}

	@Test
	public void findAllGenres() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844).setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId);
		List<Genre> allGenres = filmService.findAllGenres();
		assertNotNull(allGenres);
		assertTrue(CollectionUtils.isNotEmpty(allGenres));
		mvc.perform(MockMvcRequestBuilders.get(GET_ALL_GENRES_URI)
				.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Is.is("Action")));
	}

	@Test
	public void findTmdbFilmByTitre() throws Exception {
		String titre = "PARASITE";
		Film film = new Film();
		film.setTitre(titre);
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SEARCH_TMDB_FILM_BY_TITRE + titre)
				.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is("PARASITE")));
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
	}

	@Test
	public void findById() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION).setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SEARCH_FILM_BY_ID + film.getId())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
	}

	@Test
	public void findAllRealisateurs() throws Exception {
		filmService.cleanAllFilms();
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844).setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD).setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0,FilmOrigine.DVD);
		List<Personne> allRealisateur = filmService.findAllRealisateurs(filmDisplayTypeParam);
		assertNotNull(allRealisateur);
		if (CollectionUtils.isNotEmpty(allRealisateur)) {
			Personne realisateur = allRealisateur.get(0);
			ResultActions resultActions = mvc
					.perform(MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_URI)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(realisateur.getNom())));
			assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
		}
	}

	@Test
	public void findAllRealisateursByOrigine() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0, FilmOrigine.TOUS);
		List<Personne> allRealisateurs = filmService.findAllRealisateursByFilmDisplayType(filmDisplayTypeParam);
		assertNotNull("allRealisateurs size should be 1", allRealisateurs);
		assertTrue("allRealisateurs size should be 1", allRealisateurs.size() == 1);
		Personne real1 = allRealisateurs.get(0);
		mvc.perform(MockMvcRequestBuilders
				.get(SEARCH_ALL_REALISATEUR_BY_ORIGINE_URI + FilmOrigine.DVD.name()).param("displayType", FilmDisplayType.TOUS.name())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(real1.getNom())));
	}

	@Test
	public void findAllPersonne() throws Exception {
		filmService.cleanAllFilms();
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		List<Personne> allPersonne = personneService.findAllPersonne();
		assertNotNull(allPersonne);
		if (CollectionUtils.isNotEmpty(allPersonne)) {
			Personne personne = allPersonne.get(0);
			ResultActions resultActions = mvc
					.perform(
							MockMvcRequestBuilders.get(SEARCH_ALL_PERSONNE_URI).contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(personne.getNom())));
			assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
		}
	}

	@Test
	public void findAllActeurs() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0,FilmOrigine.DVD);
		List<Personne> allActeur = filmService.findAllActeurs(filmDisplayTypeParam);
		assertNotNull(allActeur);
		assertNotNull("allActeur size should be 3", allActeur);
		assertTrue("allActeur size should be 3", allActeur.size() == 3);
		Personne acteur1 = allActeur.get(0);
		Personne acteur2 = allActeur.get(1);
		Personne acteur3 = allActeur.get(2);
		ResultActions resultActions = mvc
				.perform(MockMvcRequestBuilders.get(SEARCH_ALL_ACTTEUR_URI).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(acteur1.getNom())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].nom", Is.is(acteur2.getNom())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].nom", Is.is(acteur3.getNom())));
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
	}

	@Test
	public void findAllActeursByOrigine() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0,FilmOrigine.DVD);
		List<Personne> allActeur = filmService.findAllActeursByFilmDisplayType(filmDisplayTypeParam);
		assertNotNull("allActeur size should be 3", allActeur);
		assertTrue("allActeur size should be 3", allActeur.size() == 3);
		Personne acteur1 = allActeur.get(0);
		Personne acteur2 = allActeur.get(1);
		Personne acteur3 = allActeur.get(2);
		ResultActions resultActions = mvc
				.perform(MockMvcRequestBuilders
						.get(SEARCH_ALL_ACTTEUR_BY_ORIGINE_URI + FilmOrigine.DVD.name()).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(acteur1.getNom())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].nom", Is.is(acteur2.getNom())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].nom", Is.is(acteur3.getNom())));
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
	}

	@Test
	@Transactional
	public void testCheckIfTmdbFilmExists() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2).setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SEARCH_FILM_BY_TMDBID + film.getTmdbId())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(Boolean.TRUE)));
		assertNotNull(resultActions);

	}

	@Test
	@Transactional
	public void testReplaceFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.TMDBID1_DATE_SORTIE).setDateInsertion(FilmBuilder.createDateInsertion(new Date(), null))
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		// FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET,
		// FilmOrigine.DVD);
		String filmJsonString = mapper.writeValueAsString(film);
		mvc.perform(MockMvcRequestBuilders
				.put(UPDATE_TMDB_FILM_BY_TMDBID + FilmBuilder.tmdbId1, film).contentType(MediaType.APPLICATION_JSON)
				.content(filmJsonString)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		Film filmUpdated = filmService.findFilm(film.getId());
		FilmBuilder.assertFilmIsNotNull(filmUpdated, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD,
				FilmBuilder.TMDBID1_DATE_SORTIE, FilmBuilder.createDateInsertion(null, null));
		Results results = client.retrieveTmdbSearchResultsById(FilmBuilder.tmdbId1);
		assertEquals(StringUtils.upperCase(results.getTitle()), filmUpdated.getTitre());
		assertEquals(POSTER_PATH, filmUpdated.getPosterPath());
	}

	@Test
	@Transactional
	public void testUpdateFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.getDvd().setRipped(false);
		FilmBuilder.assertFilmIsNotNull(filmToUpdate, true, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		mvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_4780)));
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertFalse(filmUpdated.getDvd().isRipped());
		FilmDisplayTypeParam enSalleDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.EN_SALLE);
		FilmDisplayTypeParam dvdDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.DVD);
		FilmBuilder.assertCacheSize(0, 0, enSalleDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(enSalleDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(enSalleDisplayTypeParam));
		FilmBuilder.assertCacheSize(3, 1, dvdDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(dvdDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(dvdDisplayTypeParam));
	}

	@Test
	@Transactional
	public void testUpdateFilmFromEnSalleToGoolgePlay() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1).setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setZone(new Integer(1))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		//FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.setOrigine(FilmOrigine.GOOGLE_PLAY);
		FilmBuilder.assertFilmIsNotNull(filmToUpdate, true, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		mvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_4780)));
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertFalse(filmUpdated.getDvd().isRipped());
		FilmDisplayTypeParam enSalleDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.EN_SALLE);
		FilmDisplayTypeParam googlePlayDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.GOOGLE_PLAY);
		FilmBuilder.assertCacheSize(0, 0, enSalleDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(enSalleDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(enSalleDisplayTypeParam));
		FilmBuilder.assertCacheSize(3, 1, googlePlayDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(googlePlayDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(googlePlayDisplayTypeParam));
	}
	@Test(expected = java.lang.Exception.class)
	@Transactional
	public void testRemoveFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844).setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2).setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		Film filmToRemove = filmService.findFilm(film.getId());
		assertNotNull(filmToRemove);
		logger.debug("filmToRemove=" + filmToRemove.toString());

		mvc.perform(MockMvcRequestBuilders.put(REMOVE_FILM_URI + film.getId())
				.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmRemoved = filmService.findFilm(filmToRemove.getId());
		assertNull(filmRemoved);
		FilmDisplayTypeParam dvdDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.DVD);
		FilmBuilder.assertCacheSize(0, 0, dvdDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(dvdDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(dvdDisplayTypeParam));
	}

	@Test
	@Transactional
	public void testTransfertEnSalleToDvdFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1).setGenre2(genre2).setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		/*
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
		filmToUpdate.getDvd().setRipped(true);*/
		Film filmToUpdate = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		filmToUpdate.setActeurs(film.getActeurs());
		filmToUpdate.setRealisateurs(film.getRealisateurs());
		filmToUpdate.setId(filmId);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		mvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		Film filmUpdated = filmService.findFilm(film.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmUpdated.getTitre());
		assertEquals(FilmOrigine.DVD, filmUpdated.getOrigine());
		assertEquals(DvdFormat.DVD, filmUpdated.getDvd().getFormat());
		assertEquals(new Integer(2), filmUpdated.getDvd().getZone());
		assertTrue(filmUpdated.getDvd().isRipped());
		FilmDisplayTypeParam enSalleDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.EN_SALLE);
		FilmDisplayTypeParam dvdDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.DVD);
		FilmBuilder.assertCacheSize(0, 0, enSalleDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(enSalleDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(enSalleDisplayTypeParam));
		FilmBuilder.assertCacheSize(3, 1, dvdDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(dvdDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(dvdDisplayTypeParam));
	}

	@Test
	@Transactional
	@Ignore
	public void testUpdateWithRestSaveFilm() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(SAVE_FILM_URI + FilmBuilder.tmdbId3)
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		String response = mvc.perform(builder).andReturn().getResponse().getContentAsString();
		logger.debug("response=" + response);
		Film filmToUpdate = mapper.readValue(response, Film.class);
		logger.debug("filmToUpdate=" + filmToUpdate);
		filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.getDvd().setRipped(true);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		mvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + filmToUpdate.getId(), filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertTrue(filmUpdated.getDvd().isRipped());
	}

	@Test
	@Transactional
	public void testSaveNewFilmDvd() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put(SAVE_FILM_URI + FilmBuilder.tmdbId2)
				.contentType(MediaType.APPLICATION_JSON).content(FilmOrigine.DVD.name())).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		Film filmSaved = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		// FilmBuilder.assertFilmIsNotNull(filmSaved, false,
		// FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD);
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmSaved.getTitre());
		String dateInsertion = FilmBuilder.createDateInsertion(null, null);
		FilmBuilder.assertFilmIsNotNull(filmSaved, true, 0, FilmOrigine.DVD, FilmBuilder.TMDBID2_DATE_SORTIE, dateInsertion);
	}

	@Test
	@Transactional
	public void testSaveNewFilmEnSalle() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put(SAVE_FILM_URI + FilmBuilder.tmdbId2)
				.contentType(MediaType.APPLICATION_JSON).content(FilmOrigine.EN_SALLE.name())).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		Film filmSaved = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		// FilmBuilder.assertFilmIsNotNull(filmSaved, false,
		// FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE);
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmSaved.getTitre());
		FilmDisplayTypeParam enSalleDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.EN_SALLE);
		FilmDisplayTypeParam dvdDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.DVD);
		FilmBuilder.assertCacheSize(12, 1, enSalleDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(enSalleDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(enSalleDisplayTypeParam));
		FilmBuilder.assertCacheSize(0, 0, dvdDisplayTypeParam,filmService.findAllActeursByFilmDisplayType(dvdDisplayTypeParam), filmService.findAllRealisateursByFilmDisplayType(dvdDisplayTypeParam));
	}

	@Test
	@Transactional
	public void testFindPersonne() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null);
		Personne personne = personneService.findPersonneByName(FilmBuilder.REAL_NOM_TMBD_ID_844);
		mvc.perform(MockMvcRequestBuilders.get(SEARCH_PERSONNE_URI)
				.param("nom", personne.getNom()).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.nom", Is.is(personne.getNom())));
	}

	@Test
	@Transactional
	public void testUpdatePersonne() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD).setGenre1(genre1)
				.setGenre2(genre2).setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null);
		Personne personne = personneService.findPersonneByName(FilmBuilder.ACT1_TMBD_ID_844);
		assertNotNull(personne);
		personne.setNom(FilmBuilder.ACT2_TMBD_ID_844);
		String personneJsonString = mapper.writeValueAsString(personne);
		mvc.perform(MockMvcRequestBuilders
				.put(UPDATE_PERSONNE_URI + personne.getId(), personne).contentType(MediaType.APPLICATION_JSON)
				.content(personneJsonString)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Personne personneUpdated = personneService.loadPersonne(personne.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.ACT2_TMBD_ID_844), personneUpdated.getNom());
	}

	@Test
	public void testExportFilmList() throws Exception {
		filmService.cleanAllFilms();
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setVu(true).setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780).setRipped(true)
				.setVu(true).setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.BLUERAY).setOrigine(FilmOrigine.DVD).setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2)).setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2)).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		// FilmBuilder.assertFilmIsNotNull(film2, false, FilmBuilder.RIP_DATE_OFFSET2,
		// FilmOrigine.DVD);
		byte[] b = mvc.perform(MockMvcRequestBuilders.post(EXPORT_FILM_LIST_URI)
				.content(FilmOrigine.DVD.name())).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse().getContentAsByteArray();
		assertNotNull(b);
		Workbook workbook = excelFilmHandler.createSheetFromByteArray(b);
		workbook.forEach(sheet -> {
			assertEquals(SHEET_NAME, sheet.getSheetName());
		});
		Sheet sheet = workbook.getSheetAt(0);
		assertEquals(SHEET_NAME, sheet.getSheetName());
		DataFormatter dataFormatter = new DataFormatter();
		sheet.forEach(row -> {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE);
			if (row.getRowNum() == 2) {
				row.forEach(cell -> {
					String cellValue = dataFormatter.formatCellValue(cell);
					if (cell.getColumnIndex() == 0) {
						assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_4780, cellValue);
					}
					if (cell.getColumnIndex() == 1) {
						assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780),
								StringUtils.upperCase(cellValue));
					}
					if (cell.getColumnIndex() == 2) {
						assertEquals(FilmBuilder.ANNEE.toString(), cellValue);
					}
					if (cell.getColumnIndex() == 3) {
						assertEquals(FilmBuilder.ACT3_TMBD_ID_4780 + "," + FilmBuilder.ACT1_TMBD_ID_4780 + ","
								+ FilmBuilder.ACT2_TMBD_ID_4780, cellValue);
					}
					if (cell.getColumnIndex() == 4) {
						assertEquals(FilmOrigine.DVD.name(), cellValue);
					}
					if (cell.getColumnIndex() == 5) {
						assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
					}
					if (cell.getColumnIndex() == 6) {
						assertEquals("oui", cellValue);
					}
					if(cell.getColumnIndex()==7) {
						final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
					if (cell.getColumnIndex() == 8) {
						assertEquals(FilmBuilder.ZONE_DVD, cellValue);
					}
					if (cell.getColumnIndex() == 9) {
						assertEquals("oui", cellValue);
					}
					if (cell.getColumnIndex() == 10) {
						final DateFormatter df = new DateFormatter("dd/MM/yyyy");
						assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2), Locale.FRANCE),
								cellValue);
					}
					if (cell.getColumnIndex() == 11) {
						assertEquals(DvdFormat.BLUERAY.name(), cellValue);
					}
					if (cell.getColumnIndex() == 12) {
						assertEquals(StringUtils.EMPTY, cellValue);
					}
				});
			} else if (row.getRowNum() == 1) {
				row.forEach(cell -> {
					String cellValue = dataFormatter.formatCellValue(cell);
					if (cell.getColumnIndex() == 0) {
						assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_844, cellValue);
					}
					if (cell.getColumnIndex() == 1) {
						assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844),
								StringUtils.upperCase(cellValue));
					}
					if (cell.getColumnIndex() == 2) {
						assertEquals(FilmBuilder.ANNEE.toString(), cellValue);
					}
					if (cell.getColumnIndex() == 3) {
						assertEquals(FilmBuilder.ACT1_TMBD_ID_844 + "," + FilmBuilder.ACT2_TMBD_ID_844 + ","
								+ FilmBuilder.ACT3_TMBD_ID_844, cellValue);
					}
					if (cell.getColumnIndex() == 4) {
						assertEquals(FilmOrigine.DVD.name(), cellValue);
					}
					if (cell.getColumnIndex() == 5) {
						assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
					}
					if (cell.getColumnIndex() == 6) {
						assertEquals("oui", cellValue);
					}
					if(cell.getColumnIndex()==7) {
						final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
					if (cell.getColumnIndex() == 8) {
						assertEquals(FilmBuilder.ZONE_DVD, cellValue);
					}
					if (cell.getColumnIndex() == 9) {
						assertEquals("oui", cellValue);
					}
					if (cell.getColumnIndex() == 10) {
						final DateFormatter df = new DateFormatter("dd/MM/yyyy");
						assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET), Locale.FRANCE),
								cellValue);
					}
					if (cell.getColumnIndex() == 11) {
						assertEquals(DvdFormat.DVD.name(), cellValue);
					}
					if (cell.getColumnIndex() == 12) {
						final DateFormatter df = new DateFormatter("dd/MM/yyyy");
						Date sortie = null;
						try {
							sortie = sdf.parse(FilmBuilder.DVD_DATE_SORTIE);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						assertEquals(df.print(sortie, Locale.FRANCE), cellValue);
					}
				});
			}
		});
	}
	
	@Test
	public void findFilmListParamByFilmDisplayType() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_1271)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_1271)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_1271)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(new Integer(2))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId3);
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(new Integer(2))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId4 = filmService.saveNewFilm(film4);
		FilmBuilder.assertFilmIsNotNull(film4, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId4);
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.TV)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.build();
		Long filmId5 = filmService.saveNewFilm(film5);
		FilmBuilder.assertFilmIsNotNull(film5, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.TV, FilmBuilder.FILM_DATE_SORTIE, null);
		assertNotNull(filmId5);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.DVD);
		List<Film> dvdFilms = filmService.findAllFilmsByFilmDisplayType(filmDisplayTypeParam);
		assertNotNull("dvdFilms size should be 3", dvdFilms);
		
		final String realisateurs = mvc
				.perform(MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_BY_ORIGINE_URI + FilmOrigine.DVD.name()).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		
		final String films = mvc.perform(MockMvcRequestBuilders
				.get(SEARCH_FILMS_BY_ORIGINE + FilmOrigine.DVD.name(), FilmDisplayType.TOUS.name()).param("displayType", FilmDisplayType.TOUS.name())
				.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		
		final String filmListParam = mvc.perform(MockMvcRequestBuilders
						.get(SEARCH_FILMS_BY_FILM_LIST_PARAM + FilmOrigine.DVD.name()).param("displayType", FilmDisplayType.TOUS.name())
						.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Personne> relisateursReaded = mapper.readValue(realisateurs, new TypeReference<List<Personne>>(){});
		List<Film> filmsReaded = mapper.readValue(films,new TypeReference<List<Film>>(){});
		FilmListParam filmListParamReaded = mapper.readValue(filmListParam,new TypeReference<FilmListParam>(){});
		assertNotNull(relisateursReaded);
		assertNotNull(filmsReaded);
		assertNotNull(filmListParamReaded);
		assertEquals(relisateursReaded,filmListParamReaded.getRealisateurs());
		assertEquals(filmsReaded,filmListParamReaded.getFilms());
	}
}
