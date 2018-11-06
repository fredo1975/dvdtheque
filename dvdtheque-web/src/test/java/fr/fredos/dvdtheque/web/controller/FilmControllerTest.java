package fr.fredos.dvdtheque.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.dto.ActeurDto;
import fr.fredos.dvdtheque.service.dto.FilmDto;
import fr.fredos.dvdtheque.service.dto.FilmUtils;
import fr.fredos.dvdtheque.service.dto.PersonneDto;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	private MockMvc mvc;
	@Autowired
	protected FilmService filmService;
	private final static String MAX_ID_SQL = "SELECT f.id, titre " + 
			"FROM (SELECT MAX( id ) AS id FROM FILM )f INNER JOIN FILM f2 ON f2.id = f.id";
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@Test
	public void findAllFilms() throws Exception {
		Film film = new Film();
		film.setTitre("2046");
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/dvdtheque/films")
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
		//ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/films").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film.getTitre())));
		//assertNotNull(resultActions);
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
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/dvdtheque/films/byId/"+film.getId())
				.contentType(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		assertNotNull(resultActions);
		
	}
	@Test
	public void findAllRealisateurs() throws Exception {
		//PersonneDto realisateur = retrieveRealisateur().get(0);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/dvdtheque/realisateurs")
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
		/*
		assertNotNull(realisateur);
		assertNotNull(realisateur.getId());
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/realisateurs").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(realisateur.getId())));
		assertNotNull(resultActions);*/
	}
	@Test
	public void findAllActeurs() throws Exception {
		//PersonneDto acteur = retrieveActeur().get(0);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/dvdtheque/acteurs")
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
		
		/*
		assertNotNull(acteur);
		assertNotNull(acteur.getId());
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/acteurs").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(acteur.getId())));
		assertNotNull(resultActions);*/
	}
	@Test
	@Transactional
	public void testUpdateFilm() throws Exception {
		Film film = retrieveIdAndTitreFilm().get(0);
		Integer id = null;
		if(film==null) {
			FilmDto filmDto = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
			assertNotNull(filmDto);
			id = filmDto.getId();
		}else {
			id = film.getId();
		}
		FilmDto filmToUpdate = filmService.findFilm(id);
		assertNotNull(filmToUpdate);
		logger.debug("filmToUpdate="+filmToUpdate.toString());
		filmToUpdate.setTitre(FilmUtils.TITRE_FILM);
		
		ObjectMapper mapper = new ObjectMapper();
		String filmJsonString = mapper.writeValueAsString(filmToUpdate);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put("/dvdtheque/films/byId/"+film.getId(),film.getId(),filmToUpdate)
				.contentType(MediaType.APPLICATION_JSON).content(filmJsonString);
		mvc.perform(builder)
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andDo(MockMvcResultHandlers.print());
		FilmDto filmUpdated = filmService.findFilm(id);
		assertEquals(FilmUtils.TITRE_FILM, filmUpdated.getTitre());
	}
}
