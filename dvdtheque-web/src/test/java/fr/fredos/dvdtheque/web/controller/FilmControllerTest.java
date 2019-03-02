package fr.fredos.dvdtheque.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	private MockMvc mvc;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
    private TmdbServiceClient client;
	private final static String MAX_ID_SQL = "SELECT f.id, titre " + 
			"FROM (SELECT MAX( id ) AS id FROM FILM )f INNER JOIN FILM f2 ON f2.id = f.id";
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private static final String UPDATE_FILM_URI = "/dvdtheque/films/";
	private static final String UPDATE_PERSONNE_URI = "/dvdtheque/personnes/byId/";
	private static final String SEARCH_PERSONNE_URI = "/dvdtheque/films/byPersonne";
	private static final String SEARCH_ALL_REALISATEUR_URI = "/dvdtheque/realisateurs";
	private static final String SEARCH_ALL_ACTTEUR_URI = "/dvdtheque/acteurs";
	private static final String SEARCH_FILM_BY_ID = "/dvdtheque/films/byId/";
	private static final String SEARCH_TMDB_FILM_BY_TITRE = "/dvdtheque/films/tmdb/byTitre/";
	private static final String UPDATE_TMDB_FILM_BY_TMDBID = "/dvdtheque/films/tmdb/";
	private static final String SEARCH_ALL_PERSONNE_URI = "/dvdtheque//personnes";
	private Long tmdbId= new Long(4780);
	private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w500/xghbwWlA9uW4bjkUCtUDaIeOvQ4.jpg";
	
	public static final String TITRE_FILM = "Lorem Ipsum";
	public static final String TITRE_FILM_UPDATED = "Lorem Ipsum updated";
	public static final Integer ANNEE = 2015;
	public static final String REAL_NOM = "toto titi";
	public static final String REAL_NOM1 = "Dan VanHarp";
	public static final String ACT1_NOM = "tata tutu";
	public static final String ACT2_NOM = "toitoi tuitui";
	public static final String ACT3_NOM = "tuotuo tmitmi";
	public static final String ACT4_NOM = "Graham Collins";
	
	private void assertFilmIsNotNull(Film film) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()==3);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
	}
	@Test
	public void findAllFilms() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		List<Film> allFilms = filmService.findAllFilms();
		assertNotNull(allFilms);
		if(CollectionUtils.isNotEmpty(allFilms)) {
			assertTrue(allFilms.size()>0);
			Film filmToTest = allFilms.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/dvdtheque/films")
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/films")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(filmToTest.getTitre())));
			assertNotNull(resultActions);
		}
	}
	@Test
	public void findAllTmdbFilms() throws Exception {
		String titre = "Broadway";
		Film film = new Film();
		film.setTitre(titre);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_TMDB_FILM_BY_TITRE+titre)
				.contentType(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is("Broadway Idiot")));
		assertNotNull(resultActions);
	}
	
	@Test
	public void findById() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_FILM_BY_ID+film.getId())
				.contentType(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		assertNotNull(resultActions);
		
	}
	@Test
	public void findAllRealisateurs() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		List<Personne> allRealisateur = personneService.findAllRealisateur();
		assertNotNull(allRealisateur);
		if(CollectionUtils.isNotEmpty(allRealisateur)) {
			Personne realisateur = allRealisateur.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SEARCH_ALL_REALISATEUR_URI)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(realisateur.getId())));
			assertNotNull(resultActions);
		}
	}
	@Test
	public void findAllPersonne() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		List<Personne> allPersonne = personneService.findAllPersonne();
		assertNotNull(allPersonne);
		if(CollectionUtils.isNotEmpty(allPersonne)) {
			Personne personne = allPersonne.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_PERSONNE_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SEARCH_ALL_PERSONNE_URI)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(personne.getId())));
			assertNotNull(resultActions);
		}
	}
	@Test
	public void findAllActeurs() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		List<Personne> allActeur = personneService.findAllActeur();
		assertNotNull(allActeur);
		if(CollectionUtils.isNotEmpty(allActeur)) {
			Personne acteur = allActeur.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_ALL_ACTTEUR_URI)
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SEARCH_ALL_ACTTEUR_URI)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(acteur.getId())));
			assertNotNull(resultActions);
		}
	}
	@Test
	@Transactional
	public void testReplaceFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(film);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put(UPDATE_TMDB_FILM_BY_TMDBID+tmdbId,film)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(film.getId())));;
		Film filmUpdated = filmService.findFilm(film.getId());
		Results results = client.retrieveTmdbSearchResultsById(tmdbId);
		assertEquals(StringUtils.upperCase(results.getTitle()), filmUpdated.getTitre());
		assertEquals(POSTER_PATH, filmUpdated.getPosterPath());
	}
	@Test
	@Transactional
	public void testUpdateFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		Film filmToUpdate = filmService.findFilm(film.getId());
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate="+filmToUpdate.toString());
		filmToUpdate.setTitre(TITRE_FILM_UPDATED);
		filmToUpdate.setRipped(false);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put(UPDATE_FILM_URI+film.getId(),filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmUpdated = filmService.findFilm(film.getId());
		assertEquals(StringUtils.upperCase(TITRE_FILM_UPDATED), filmUpdated.getTitre());
		assertFalse(filmUpdated.isRipped());
	}
	@Test
	@Transactional
	public void testSaveNewFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		Film filmToSave = new Film();
		filmToSave.setTitre(TITRE_FILM_UPDATED);
		filmToSave.setActeurs(film.getActeurs());
		filmToSave.setRealisateurs(film.getRealisateurs());
		filmToSave.setAnnee(1987);
		Dvd dvd = new Dvd();
		dvd.setAnnee(1978);
		dvd.setEdition("ds");
		dvd.setZone(1);
		filmToSave.setDvd(dvd);
		filmToSave.setPosterPath("posterPath");
		filmToSave.setTmdbId(100l);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(filmToSave);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.post(UPDATE_FILM_URI,filmToSave)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmSaved = filmService.findFilmByTitre(TITRE_FILM_UPDATED);
		assertFilmIsNotNull(filmSaved);
		assertEquals(StringUtils.upperCase(TITRE_FILM_UPDATED),filmSaved.getTitre());
	}
	@Test
	@Transactional
	public void testFindPersonne() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		Personne personne = personneService.findPersonneByFullName(REAL_NOM);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.get(SEARCH_PERSONNE_URI)
				.param("nom", personne.getNom())
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(personne.getId())));
	}
	@Test
	@Transactional
	public void testUpdatePersonne() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		Personne personne = personneService.findPersonneByFullName(ACT1_NOM);
		assertNotNull(personne);
		personne.setNom(ACT2_NOM);
		ObjectMapper mapper = new ObjectMapper();
		String personneJsonString = mapper.writeValueAsString(personne);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put(UPDATE_PERSONNE_URI+personne.getId(),personne)
				.contentType(MediaType.APPLICATION_JSON).content(personneJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
		Personne personneUpdated = personneService.loadPersonne(personne.getId());
		assertEquals(StringUtils.upperCase(ACT2_NOM), personneUpdated.getNom());
	}
}
