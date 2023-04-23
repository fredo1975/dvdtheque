package fr.fredos.dvdtheque.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.tmdb.model.Cast;
import fr.fredos.dvdtheque.common.tmdb.model.Credits;
import fr.fredos.dvdtheque.common.tmdb.model.Crew;
import fr.fredos.dvdtheque.common.tmdb.model.Genres;
import fr.fredos.dvdtheque.common.tmdb.model.Results;
import fr.fredos.dvdtheque.integration.config.ContextConfiguration;
import fr.fredos.dvdtheque.integration.config.HazelcastConfiguration;
import fr.fredos.dvdtheque.rest.allocine.model.CritiquePresseDto;
import fr.fredos.dvdtheque.rest.allocine.model.FicheFilmDto;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import fr.fredos.dvdtheque.rest.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.rest.model.ExcelFilmHandler;
import fr.fredos.dvdtheque.rest.service.IFilmService;
import fr.fredos.dvdtheque.rest.service.IPersonneService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ContextConfiguration.class,HazelcastConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger 								logger = LoggerFactory.getLogger(FilmControllerTest.class);
	@Autowired
	protected IFilmService 							filmService;
	@Autowired
	protected IPersonneService 						personneService;
	@Autowired
	private ObjectMapper 							mapper;
	@Autowired
	private ExcelFilmHandler 						excelFilmHandler;
    private MockRestServiceServer 					mockServer;
    @Autowired
    private Environment 							environment;
    @Autowired
	private MockMvc 								mockmvc;
	@MockBean
	private JwtDecoder 								jwtDecoder;
	@Autowired
	private RestTemplate 							restTemplate;
	
	public static final MediaType 					APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));
	
	private static final String 					GET_ALL_FILMS_URI = "/dvdtheque-service/films/";
	private static final String 					GET_CLEAN_ALL_FILMS_URI = GET_ALL_FILMS_URI+"cleanAllfilms/";
	private static final String 					GET_ALL_GENRES_URI = GET_ALL_FILMS_URI+"genres/";
	private static final String 					UPDATE_PERSONNE_URI = "/dvdtheque-service/personnes/byId/";
	private static final String 					SEARCH_PERSONNE_URI = GET_ALL_FILMS_URI + "byPersonne";
	private static final String 					SEARCH_BY_ID_ALLOCINE_CRITIQUE_URI = "/dvdtheque-service/films/allocine/byId";
	private static final String 					SEARCH_ALL_BY_TITLE_ALLOCINE_CRITIQUE_URI = "/dvdtheque-service/films/allocine/byTitle";
	private static final String 					SEARCH_FILM_BY_ID = GET_ALL_FILMS_URI + "byId/";
	private static final String 					SEARCH_FILM_BY_TMDBID = GET_ALL_FILMS_URI + "byTmdbId/";
	private static final String 					SEARCH_TMDB_FILM_BY_TITRE = GET_ALL_FILMS_URI + "tmdb/byTitre/";
	private static final String 					SEARCH_FILMS_BY_QUERY_PARAM = "/dvdtheque-service/films/search";
	private static final String 					PAGINATED_SEARCH_FILMS_BY_QUERY_PARAM = "/dvdtheque-service//films/paginatedSarch";
	private static final String 					SAVE_FILM_URI = GET_ALL_FILMS_URI + "save/";
	private static final String 					UPDATE_FILM_URI = GET_ALL_FILMS_URI + "update/";
	private static final String 					REMOVE_FILM_URI = GET_ALL_FILMS_URI + "remove/";
	private static final String 					RETRIEVE_FILM_IMAGE_URI = GET_ALL_FILMS_URI + "retrieveImage/";
	private static final String 					RETRIEVE_ALL_FILM_IMAGE_URI = GET_ALL_FILMS_URI + "retrieveAllImages";
	private static final String 					CLEAN_ALL_CACHES_URI = GET_ALL_FILMS_URI + "cleanCaches/";
	private static final String 					SEARCH_ALL_PERSONNE_URI = "/dvdtheque-service/personnes";
	private static final String 					EXPORT_FILM_LIST_URI = GET_ALL_FILMS_URI + "export";
	private static final String 					EXPORT_FILM_SEARCH_URI = GET_ALL_FILMS_URI + "search/export";
	public static final String 						SHEET_NAME = "Films";
	
	public static final String TMDB_RELEASE_DATE = "2007-03-21T00:00:00.000+00:00";
	@BeforeEach()
	public void setUp() throws Exception {
		mockServer = MockRestServiceServer.createServer(restTemplate);
		filmService.cleanAllFilms();
	}
	@Test
	@WithMockUser(roles = "user")
	public void getAllFilms() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		assertNotNull(filmService.saveNewFilm(film));
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		mockmvc.perform(MockMvcRequestBuilders.get(GET_ALL_FILMS_URI)
				.with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
	}
	@Test
	@WithMockUser(roles = "user")
	public void findAllCritiquePresseByAllocineFilmById() throws Exception {
		FicheFilmDto film = new FicheFilmDto();
		film.setCritiquePresse(new HashSet<>());
		CritiquePresseDto cp = new CritiquePresseDto();
		film.getCritiquePresse().add(cp);
		mockServer.expect(ExpectedCount.once(),
		          requestTo(environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_URL)+environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_BY_ID)+"?id=0"))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(film), MediaType.APPLICATION_JSON));
		
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_BY_ID_ALLOCINE_CRITIQUE_URI)
				.param("id", String.valueOf(0))
				.with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	@WithMockUser(roles = "user")
	public void findAllCritiquePresseByAllocineFilmByTitle() throws Exception {
		FicheFilmDto film1 = new FicheFilmDto();
		film1.setCritiquePresse(new HashSet<>());
		CritiquePresseDto cp = new CritiquePresseDto();
		film1.getCritiquePresse().add(cp);
		FicheFilmDto film2 = new FicheFilmDto();
		film2.setCritiquePresse(new HashSet<>());
		CritiquePresseDto cp2 = new CritiquePresseDto();
		film2.getCritiquePresse().add(cp2);
		List<FicheFilmDto> l = List.of(film1,film2);
		mockServer.expect(ExpectedCount.once(),
		          requestTo(environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_URL)+environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_BY_TITLE)+"?title=title&titleO=title"))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(l), MediaType.APPLICATION_JSON));
		
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_ALL_BY_TITLE_ALLOCINE_CRITIQUE_URI)
				.param("title", "title")
				.with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@WithMockUser(roles = "user")
	@Test
	public void cleanAllFilms() throws Exception {
		mockmvc.perform(MockMvcRequestBuilders.put(GET_CLEAN_ALL_FILMS_URI).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(status().isOk());
	}
	@WithMockUser(roles = "user")
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
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_1271)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_1271)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_1271)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(Integer.valueOf(2))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId3);
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setZone(Integer.valueOf(2))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId4 = filmService.saveNewFilm(film4);
		FilmBuilder.assertFilmIsNotNull(film4, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null, true);
		assertNotNull(filmId4);
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setOrigine(FilmOrigine.TV)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId5 = filmService.saveNewFilm(film5);
		FilmBuilder.assertFilmIsNotNull(film5, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.TV, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId5);
		
		var query = "origine:eq:"+FilmOrigine.DVD+":AND";
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_FILMS_BY_QUERY_PARAM)
				.param("query", query)
				.param("offset", String.valueOf(1))
				.param("limit", String.valueOf(20))
				.param("sort", "-titre")
				.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[1].titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_4780)));
		
		query = "origine:eq:"+FilmOrigine.EN_SALLE+":AND";
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_FILMS_BY_QUERY_PARAM)
				.param("query", query)
				.param("offset", String.valueOf(1))
				.param("limit", String.valueOf(20))
				.param("sort", "+titre")
				.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[1].titre", Is.is(StringUtils.upperCase(FilmBuilder.TITRE_FILM_REREUPDATED))));
		
		query = "origine:eq:"+FilmOrigine.TV+":AND";
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_FILMS_BY_QUERY_PARAM)
				.param("query", query)
				.param("offset", String.valueOf(1))
				.param("limit", String.valueOf(20))
				.param("sort", "-titre")
				.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(StringUtils.upperCase(FilmBuilder.TITRE_FILM_REREREUPDATED))));
	}
	
	@Test
	public void findAllGenres() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId);
		List<Genre> allGenres = filmService.findAllGenres();
		assertNotNull(allGenres);
		assertTrue(CollectionUtils.isNotEmpty(allGenres));
		
		mockmvc.perform(MockMvcRequestBuilders.get(GET_ALL_GENRES_URI).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf())).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Is.is("Action")));
		
	}

	@Test
	public void findTmdbFilmByTitre() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId);
		
		final Set<Results> results = new HashSet<>();
		Results res = new Results();
		res.setTitle(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		res.setId(film.getTmdbId());
		results.add(res);
		
		
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_BY_TITLE)+"?title="+FilmBuilder.TITRE_FILM_TMBD_ID_844))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(results), MediaType.APPLICATION_JSON));
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_RELEASE_DATE)+"?tmdbId="+res.getId()))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString("2015-08-01T23:00:00.000+00:00"), MediaType.APPLICATION_JSON));
		Credits credits = new Credits();
		Crew crew = new Crew();
		crew.setCredit_id("52fe42ecc3a36847f802d26d");
		crew.setName("William Hoy");
		crew.setJob("Director");
		credits.setCrew(Lists.newArrayList(crew));
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
							+environment.getRequiredProperty(FilmController.TMDB_SERVICE_CREDITS)+"?tmdbId="+res.getId()))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(credits), MediaType.APPLICATION_JSON));
		
		ResultActions resultActions = mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_TMDB_FILM_BY_TITRE + FilmBuilder.TITRE_FILM_TMBD_ID_844).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf())).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		mockServer.verify();
        //assertEquals("{message : 'under construction'}", result);
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
	}

	private List<FicheFilmDto> buildFicheFilmDtoList(Film film, Integer allocineFicheFilmId){
		FicheFilmDto ficheFilmDto = new FicheFilmDto();
		ficheFilmDto.setAllocineFilmId(15);
		ficheFilmDto.setPageNumber(1);
		ficheFilmDto.setTitle(film.getTitre());
		ficheFilmDto.setUrl("fakeurl");
		if(allocineFicheFilmId != null) {
			ficheFilmDto.setId(allocineFicheFilmId);
		}
		
		List<FicheFilmDto> l = new ArrayList<>();
		CritiquePresseDto cp = new CritiquePresseDto();
		cp.setAuthor("author");
		cp.setBody("une critique presse bien ficellée");
		cp.setNewsSource("Telerama");
		cp.setRating(5d);
		Set<CritiquePresseDto> critiquesPresses = new HashSet<>();
		critiquesPresses.add(cp);
		ficheFilmDto.setCritiquePresse(critiquesPresses);
		l.add(ficheFilmDto);
		return l;
	}
	private void simulateAlloCineServiceCall(Film film, Integer allocineFicheFilmId) throws JsonProcessingException, IllegalStateException {
		List<FicheFilmDto> l = buildFicheFilmDtoList(film,allocineFicheFilmId);
		if(allocineFicheFilmId != null) {
			mockServer.expect(ExpectedCount.once(), 
			          requestTo(environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_URL)
								+environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_BY_ID)+"?id="+allocineFicheFilmId))
			          .andExpect(method(HttpMethod.GET))
			          .andRespond(withSuccess(mapper.writeValueAsString(l.get(0)), MediaType.APPLICATION_JSON));
		}else {
			mockServer.expect(ExpectedCount.once(), 
			          requestTo(environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_URL)
								+environment.getRequiredProperty(FilmController.ALLOCINE_SERVICE_BY_TITLE)+"?title="+film.getTitre()+"&titleO="+film.getTitreO()))
			          .andExpect(method(HttpMethod.GET))
			          .andRespond(withSuccess(mapper.writeValueAsString(l), MediaType.APPLICATION_JSON));
		}
		
	}
	@Test
	public void findByIdWithAllocineFicheFilmId() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		simulateAlloCineServiceCall(film, film.getAllocineFicheFilmId());
		
		
		ResultActions resultActions = mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_FILM_BY_ID + film.getId()).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		mockServer.verify();
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
	}
	@Test
	public void findByIdWithoutAllocineFicheFilmId() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, true);
		simulateAlloCineServiceCall(film, null);
		
		
		ResultActions resultActions = mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_FILM_BY_ID + film.getId()).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		mockServer.verify();
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		List<Personne> allPersonne = personneService.findAllPersonne();
		assertNotNull(allPersonne);
		if (CollectionUtils.isNotEmpty(allPersonne)) {
			Personne personne = allPersonne.get(0);
			
			ResultActions resultActions = mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_ALL_PERSONNE_URI).with(jwt().jwt(builder -> builder.subject("test")))
					.with(csrf()))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].nom", Is.is(personne.getNom())));
			
			assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
		}
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
				.setGenre2(genre2).setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		
		ResultActions resultActions = mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_FILM_BY_TMDBID + film.getTmdbId()).contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(Boolean.TRUE)));
		
		assertNotNull(resultActions);

	}

	@Test
	@Transactional
	public void testUpdateWithAllocineFicheFilmIdFilm() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		//filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.getDvd().setRipped(false);
		FilmBuilder.assertFilmIsNotNull(filmToUpdate, true, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		
		simulateAlloCineServiceCall(filmToUpdate, film.getAllocineFicheFilmId());
		
		mockmvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate).contentType(MediaType.APPLICATION_JSON).content(filmJsonString).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf())).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.dvd.ripped", Is.is(false)));
		mockServer.verify();
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmUpdated.getTitre());
		assertFalse(filmUpdated.getDvd().isRipped());
	}
	@Test
	@Transactional
	public void testUpdateWithoutAllocineFicheFilmIdFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1).setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, true);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		//filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.getDvd().setRipped(false);
		FilmBuilder.assertFilmIsNotNull(filmToUpdate, true, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, true);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		
		simulateAlloCineServiceCall(filmToUpdate, null);
		
		mockmvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate).contentType(MediaType.APPLICATION_JSON).content(filmJsonString).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf())).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.dvd.ripped", Is.is(false)));
		mockServer.verify();
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmUpdated.getTitre());
		assertFalse(filmUpdated.getDvd().isRipped());
	}
	@Test
	@Transactional
	public void testUpdateCritiquePresseFilm() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		
		List<FicheFilmDto> l = buildFicheFilmDtoList(filmToUpdate,film.getAllocineFicheFilmId());
		filmToUpdate.setAllocineFicheFilmId(l.get(0).getId());
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		
		simulateAlloCineServiceCall(filmToUpdate, film.getAllocineFicheFilmId());
		
		mockmvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate).contentType(MediaType.APPLICATION_JSON).content(filmJsonString).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf())).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.allocineFicheFilmId", Is.is(film.getAllocineFicheFilmId())));
		mockServer.verify();
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmUpdated.getTitre());
		assertEquals(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844, filmUpdated.getAllocineFicheFilmId());
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
				.setZone(Integer.valueOf(1))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		//FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate=" + filmToUpdate.toString());
		filmToUpdate.setOrigine(FilmOrigine.GOOGLE_PLAY);
		FilmBuilder.assertFilmIsNotNull(filmToUpdate, true, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		simulateAlloCineServiceCall(filmToUpdate, film.getAllocineFicheFilmId());
		mockmvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate).contentType(MediaType.APPLICATION_JSON).content(filmJsonString).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.origine", Is.is(FilmOrigine.GOOGLE_PLAY.name())));
		mockServer.verify();
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmUpdated.getTitre());
		assertFalse(filmUpdated.getDvd().isRipped());
	}
	@Test
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
				.setGenre2(genre2).setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film filmToRemove = filmService.findFilm(film.getId());
		assertNotNull(filmToRemove);
		logger.debug("filmToRemove=" + filmToRemove.toString());

		mockmvc.perform(MockMvcRequestBuilders.put(REMOVE_FILM_URI + film.getId()).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf())).andDo(MockMvcResultHandlers.print()).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}
	
	@Test
	@Transactional
	public void testcleanAllCaches() throws Exception {
		mockmvc.perform(MockMvcRequestBuilders.put(CLEAN_ALL_CACHES_URI).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf())).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
	}
	
	@Test
	@Transactional
    public void retrieveAllFilmImageTest() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28, "Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35, "Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setPosterPath("http://image.tmdb.org/t/p/w500/9K81OagrRukWybhIIX6iRC5IRWo.jpg")
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		
        mockServer.expect(ExpectedCount.once(), 
          requestTo(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)+"?posterPath="+film.getPosterPath()))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withSuccess("true", MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(Boolean.TRUE)));
        
        mockmvc.perform(MockMvcRequestBuilders.put(RETRIEVE_ALL_FILM_IMAGE_URI).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
		mockServer.verify();
		
    }
	@Test
	@Transactional
	public void testRetrieveFilmImage() throws Exception {
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
				.setGenre2(genre2).setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film filmToRetrieveImage = filmService.findFilm(film.getId());
		assertNotNull(filmToRetrieveImage);
		logger.debug("filmToRetrieveImage=" + filmToRetrieveImage.toString());

		Results res = new Results();
		res.setTitle(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		res.setId(film.getTmdbId());
		
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)+ environment.getRequiredProperty(FilmController.TMDB_SERVICE_RESULTS)+"?tmdbId="+res.getId()))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(res), MediaType.APPLICATION_JSON));
		
		mockmvc.perform(MockMvcRequestBuilders.put(RETRIEVE_FILM_IMAGE_URI + film.getId()).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		
		mockServer.verify();
		
		Film filmRetrievedImage = filmService.findFilm(filmToRetrieveImage.getId());
		assertNotNull(filmRetrievedImage);
		assertNotNull(filmRetrievedImage.getPosterPath());
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
				.setGenre1(genre1).setGenre2(genre2).setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		filmToUpdate.setActeur(film.getActeur());
		filmToUpdate.setRealisateur(film.getRealisateur());
		filmToUpdate.setId(filmId);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		simulateAlloCineServiceCall(filmToUpdate, filmToUpdate.getAllocineFicheFilmId());
		mockmvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + film.getId(), filmToUpdate).contentType(MediaType.APPLICATION_JSON).content(filmJsonString).with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		mockServer.verify();
		Film filmUpdated = filmService.findFilm(film.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), filmUpdated.getTitre());
		assertEquals(FilmOrigine.DVD, filmUpdated.getOrigine());
		assertEquals(DvdFormat.DVD, filmUpdated.getDvd().getFormat());
		assertEquals(Integer.valueOf(2), filmUpdated.getDvd().getZone());
		assertTrue(filmUpdated.getDvd().isRipped());
	}

	@Test
	@Transactional
	@Disabled
	public void testUpdateWithRestSaveFilm() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(SAVE_FILM_URI + FilmBuilder.tmdbId3)
				.contentType(MediaType.APPLICATION_JSON);
		
		mockmvc.perform(builder.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
		String response = mockmvc.perform(builder).andReturn().getResponse().getContentAsString();
		logger.debug("response=" + response);
		Film filmToUpdate = mapper.readValue(response, Film.class);
		logger.debug("filmToUpdate=" + filmToUpdate);
		filmToUpdate.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		filmToUpdate.getDvd().setRipped(true);
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		
		mockmvc.perform(MockMvcRequestBuilders.put(UPDATE_FILM_URI + filmToUpdate.getId(), filmToUpdate).content(filmJsonString).with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
		Film filmUpdated = filmService.findFilm(filmToUpdate.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertTrue(filmUpdated.getDvd().isRipped());
	}

	@Test
	@Transactional
	public void testSaveNewFilmDvd() throws Exception {
		Results res = new Results();
		res.setTitle(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		res.setId(FilmBuilder.tmdbId2);
		List<Genres> l = new ArrayList<>();
		Genres genres1 = new Genres();
		genres1.setName("Comedy");
		genres1.setId(35);
		l.add(genres1);
		res.setGenres(l);
		Credits credits = new Credits();
		Crew crew = new Crew();
		crew.setCredit_id("52fe42ecc3a36847f802d26d");
		crew.setName("William Hoy");
		crew.setJob("Director");
		credits.setCrew(Lists.newArrayList(crew));
		List<Cast> casts = new ArrayList<>();
		Cast cast1 = new Cast();
		cast1.setName(FilmBuilder.ACT1_TMBD_ID_844);
		Cast cast2 = new Cast();
		cast2.setName(FilmBuilder.ACT2_TMBD_ID_844);
		Cast cast3 = new Cast();
		cast3.setName(FilmBuilder.ACT3_TMBD_ID_844);
		casts.add(cast1);
		casts.add(cast2);
		casts.add(cast3);
		credits.setCast(casts);
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(new URI(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_RESULTS)+"?tmdbId="+res.getId())))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(res), MediaType.APPLICATION_JSON));
		
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(new URI(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_RELEASE_DATE)+"?tmdbId="+res.getId())))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(TMDB_RELEASE_DATE), MediaType.APPLICATION_JSON));
		
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(new URI(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_CREDITS)+"?tmdbId="+res.getId())))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(credits), MediaType.APPLICATION_JSON));
		Film film = new Film();
		film.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		simulateAlloCineServiceCall(film, null);
		mockmvc.perform(MockMvcRequestBuilders
				.put(SAVE_FILM_URI + FilmBuilder.tmdbId2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(FilmOrigine.DVD.name())
				.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		mockServer.verify();
		
		var query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_844+":AND";
		var page = filmService.paginatedSarch(query, 1, 10, "");
		assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		Film filmRetrieved = page.getContent().iterator().next();
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(filmRetrieved.getTitre());
	}

	@Test
	@Transactional
	public void testSaveNewFilmEnSalle() throws Exception {
		Results res = new Results();
		res.setTitle(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		res.setId(FilmBuilder.tmdbId2);
		List<Genres> l = new ArrayList<>();
		Genres genres1 = new Genres();
		genres1.setName("Comedy");
		genres1.setId(35);
		l.add(genres1);
		res.setGenres(l);
		Credits credits = new Credits();
		Crew crew = new Crew();
		crew.setCredit_id("52fe42ecc3a36847f802d26d");
		crew.setName("William Hoy");
		crew.setJob("Director");
		credits.setCrew(Lists.newArrayList(crew));
		List<Cast> casts = new ArrayList<>();
		Cast cast1 = new Cast();
		cast1.setName(FilmBuilder.ACT1_TMBD_ID_1271);
		Cast cast2 = new Cast();
		cast2.setName(FilmBuilder.ACT2_TMBD_ID_1271);
		Cast cast3 = new Cast();
		cast3.setName(FilmBuilder.ACT3_TMBD_ID_1271);
		casts.add(cast1);
		casts.add(cast2);
		casts.add(cast3);
		credits.setCast(casts);
		
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(new URI(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_RESULTS)+"?tmdbId="+res.getId())))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(res), MediaType.APPLICATION_JSON));
		
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(new URI(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_RELEASE_DATE)+"?tmdbId="+res.getId())))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(TMDB_RELEASE_DATE), MediaType.APPLICATION_JSON));
		
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(new URI(environment.getRequiredProperty(FilmController.TMDB_SERVICE_URL)
		        		  +environment.getRequiredProperty(FilmController.TMDB_SERVICE_CREDITS)+"?tmdbId="+res.getId())))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(credits), MediaType.APPLICATION_JSON));
		Film film = new Film();
		film.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		simulateAlloCineServiceCall(film, null);
		mockmvc.perform(MockMvcRequestBuilders
				.put(SAVE_FILM_URI + FilmBuilder.tmdbId2)
				.content(FilmOrigine.EN_SALLE.name())
				.with(jwt()
				.jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		
		mockServer.verify();
		var query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_844+":AND";
		var page = filmService.paginatedSarch(query, 1, 10, "");
		assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		Film filmRetrieved = page.getContent().iterator().next();
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(filmRetrieved.getTitre());
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		Personne personne = personneService.findPersonneByName(FilmBuilder.REAL_NOM_TMBD_ID_844);
		
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_PERSONNE_URI).param("nom", personne.getNom()).with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
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
				.setGenre2(genre2).setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		Personne personne = personneService.findPersonneByName(FilmBuilder.ACT1_TMBD_ID_844);
		assertNotNull(personne);
		personne.setNom(FilmBuilder.ACT2_TMBD_ID_844);
		String personneJsonString = mapper.writeValueAsString(personne);
		
		mockmvc.perform(MockMvcRequestBuilders.put(UPDATE_PERSONNE_URI + personne.getId(), personne).contentType(MediaType.APPLICATION_JSON).content(personneJsonString).with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
		Personne personneUpdated = personneService.loadPersonne(personne.getId());
		assertEquals(StringUtils.upperCase(FilmBuilder.ACT2_TMBD_ID_844), personneUpdated.getNom());
	}

	@Test
	public void testExportFilmSearch() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
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
				.setZone(Integer.valueOf(2)).setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2))
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		
		var query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_4780+":AND,";
		byte[] b = mockmvc.perform(MockMvcRequestBuilders.post(EXPORT_FILM_SEARCH_URI).content(query).with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andReturn().getResponse().getContentAsByteArray();
		
		assertNotNull(b);
		
		Workbook workbook = excelFilmHandler.createSheetFromByteArray(b);
		workbook.forEach(sheet -> {
			assertEquals(SHEET_NAME, sheet.getSheetName());
		});
		Sheet sheet = workbook.getSheetAt(0);
		assertEquals(SHEET_NAME, sheet.getSheetName());
		
		DataFormatter dataFormatter = new DataFormatter();
		
		sheet.forEach(row -> {
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE);
			if (row.getRowNum() == 1) {
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
						assertEquals("", cellValue);
                    }
					if(cell.getColumnIndex()==8) {
						final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
					if (cell.getColumnIndex() == 9) {
						assertEquals(FilmBuilder.ZONE_DVD, cellValue);
					}
					if (cell.getColumnIndex() == 10) {
						assertEquals("oui", cellValue);
					}
					if (cell.getColumnIndex() == 11) {
						final DateFormatter df = new DateFormatter("dd/MM/yyyy");
						assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2), Locale.FRANCE),
								cellValue);
					}
					if (cell.getColumnIndex() == 12) {
						assertEquals(DvdFormat.BLUERAY.name(), cellValue);
					}
					if (cell.getColumnIndex() == 13) {
						assertEquals(StringUtils.EMPTY, cellValue);
					}
				});
			}
		});
	}
	@Test
	//@Disabled
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
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
				.setZone(Integer.valueOf(2)).setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2))
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		
		byte[] b = mockmvc.perform(MockMvcRequestBuilders.post(EXPORT_FILM_LIST_URI).content(FilmOrigine.DVD.name()).with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andReturn().getResponse().getContentAsByteArray();
		
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
			if (row.getRowNum() == 1) {
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
						assertEquals("", cellValue);
                    }
					if(cell.getColumnIndex()==8) {
						final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
					if (cell.getColumnIndex() == 9) {
						assertEquals(FilmBuilder.ZONE_DVD, cellValue);
					}
					if (cell.getColumnIndex() == 10) {
						assertEquals("oui", cellValue);
					}
					if (cell.getColumnIndex() == 11) {
						final DateFormatter df = new DateFormatter("dd/MM/yyyy");
						assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2), Locale.FRANCE),
								cellValue);
					}
					if (cell.getColumnIndex() == 12) {
						assertEquals(DvdFormat.BLUERAY.name(), cellValue);
					}
					if (cell.getColumnIndex() == 13) {
						assertEquals(StringUtils.EMPTY, cellValue);
					}
				});
			} else if (row.getRowNum() == 2) {
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
						assertEquals("", cellValue);
                    }
					if(cell.getColumnIndex()==8) {
						final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
					if (cell.getColumnIndex() == 9) {
						assertEquals(FilmBuilder.ZONE_DVD, cellValue);
					}
					if (cell.getColumnIndex() == 10) {
						assertEquals("oui", cellValue);
					}
					if (cell.getColumnIndex() == 11) {
						final DateFormatter df = new DateFormatter("dd/MM/yyyy");
						assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET), Locale.FRANCE),
								cellValue);
					}
					if (cell.getColumnIndex() == 12) {
						assertEquals(DvdFormat.DVD.name(), cellValue);
					}
					if (cell.getColumnIndex() == 13) {
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
	public void testSearch() throws UnsupportedEncodingException, Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		assertNotNull(filmService.saveNewFilm(film));
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		final var query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_844+":AND";
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_FILMS_BY_QUERY_PARAM)
				.param("query", query)
				.param("offset", String.valueOf(1))
				.param("limit", String.valueOf(20))
				.param("sort", "-titre")
				.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		
	}
	@Test
	public void testPaginatedSarch() throws UnsupportedEncodingException, Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		assertNotNull(filmService.saveNewFilm(film));
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		final var query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_844+":AND";
		mockmvc.perform(MockMvcRequestBuilders.get(PAGINATED_SEARCH_FILMS_BY_QUERY_PARAM)
				.param("query", query)
				.param("offset", String.valueOf(1))
				.param("limit", String.valueOf(20))
				.param("sort", "-titre")
				.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("totalElements", Is.is(1)))
		.andExpect(MockMvcResultMatchers.jsonPath("content[0].titre", Is.is(FilmBuilder.TITRE_FILM_TMBD_ID_844)));
		
	}
	@Test
	public void transformTmdbFilmToDvdThequeFilm() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		
		final Set<Results> results = new HashSet<>();
		Results res = new Results();
		res.setTitle(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		res.setId(film.getTmdbId());
		results.add(res);
		
		
	}
}
