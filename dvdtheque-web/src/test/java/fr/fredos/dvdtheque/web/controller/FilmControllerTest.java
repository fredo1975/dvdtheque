package fr.fredos.dvdtheque.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import fr.fredos.dvdtheque.common.dto.NewActeurDto;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;
import fr.fredos.dvdtheque.service.dto.FilmUtils;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	private MockMvc mvc;
	@Autowired
	protected FilmService filmService;
	@Autowired
	protected PersonneService personneService;
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
	private static final String SEARCH_ALL_PERSONNE_URI = "/dvdtheque//personnes";
	@Test
	public void findAllFilms() throws Exception {
		List<Film> allFilms = filmService.findAllFilms();
		assertNotNull(allFilms);
		if(CollectionUtils.isNotEmpty(allFilms)) {
			assertTrue(allFilms.size()>0);
			Film film = allFilms.get(0);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/dvdtheque/films")
					.contentType(MediaType.APPLICATION_JSON);
			mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/films")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film.getTitre())));
			assertNotNull(resultActions);
		}
	}
	private List<Film> retrieveIdAndTitreFilm() {
		return this.jdbcTemplate.query(MAX_ID_SQL, new RowMapper<Film>() {
			@Override
			public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
				Film film=new Film();  
				film.setId(rs.getInt(1));  
				film.setTitre(rs.getString(2));  
		        return film;  
			}
		});
	}
	@Test
	public void findById() throws Exception {
		Film film = retrieveIdAndTitreFilm().get(0);
		assertNotNull(film);
		assertNotNull(film.getId());
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(SEARCH_FILM_BY_ID+film.getId())
				.contentType(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		assertNotNull(resultActions);
		
	}
	@Test
	public void findAllRealisateurs() throws Exception {
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
	public void testUpdateFilm() throws Exception {
		Film film = retrieveIdAndTitreFilm().get(0);
		Integer id = null;
		if(film==null) {
			Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
			Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
			film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
			id = filmService.saveNewFilm(film);
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
			assertNotNull(film);
			id = film.getId();
		}else {
			id = film.getId();
		}
		Film filmToUpdate = filmService.findFilm(id);
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate="+filmToUpdate.toString());
		filmToUpdate.setTitre(FilmUtils.TITRE_FILM_UPDATED);
		Set<NewActeurDto> newActeurDtoSet = new HashSet<>();
		NewActeurDto newActeurDto = FilmUtils.buildNewActeurDto();
		newActeurDtoSet.add(newActeurDto);
		filmToUpdate.setNewActeurDtoSet(newActeurDtoSet);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put(UPDATE_FILM_URI+film.getId(),filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmUpdated = filmService.findFilm(id);
		assertEquals(FilmUtils.TITRE_FILM_UPDATED, filmUpdated.getTitre());
	}
	@Test
	@Transactional
	public void testSaveNewFilm() throws Exception {
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		
		Set<NewActeurDto> newActeurDtoSet = new HashSet<>();
		NewActeurDto newActeurDto = FilmUtils.buildNewActeurDto();
		newActeurDtoSet.add(newActeurDto);
		film.setNewActeurDtoSet(newActeurDtoSet);
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(film);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.post(UPDATE_FILM_URI,film)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		Film filmSaved = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(filmSaved);
		assertNotNull(filmSaved.getTitre());
		assertEquals(StringUtils.upperCase(FilmUtils.TITRE_FILM),filmSaved.getTitre());
	}
	@Test
	@Transactional
	public void testFindPersonne() throws Exception {
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		Personne personne = personneService.findByPersonneId(idRealisateur);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.get(SEARCH_PERSONNE_URI)
				.param("nom", personne.getNom())
				.param("prenom", personne.getPrenom())
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(personne.getId())));
	}
	@Test
	@Transactional
	public void testUpdatePersonne() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(FilmUtils.MAX_PERSONNE_ID_SQL, Integer.class);
		if(id==null) {
			// insert a personne first
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM, FilmUtils.ACT1_PRENOM));
			id = this.jdbcTemplate.queryForObject(FilmUtils.MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		personneByLoad.setNom(FilmUtils.ACT2_NOM);
		personneByLoad.setPrenom(FilmUtils.ACT2_PRENOM);
		ObjectMapper mapper = new ObjectMapper();
		String personneJsonString = mapper.writeValueAsString(personneByLoad);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put(UPDATE_PERSONNE_URI+personneByLoad.getId(),personneByLoad)
				.contentType(MediaType.APPLICATION_JSON).content(personneJsonString);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
		Personne personneUpdated = personneService.loadPersonne(id);
		assertEquals(FilmUtils.ACT2_NOM, personneUpdated.getNom());
		assertEquals(FilmUtils.ACT2_PRENOM, personneUpdated.getPrenom());
	}
}
