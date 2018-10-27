package fr.fredos.dvdtheque.web.controller;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.fredos.dvdtheque.dao.model.object.Film;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	private MockMvc mvc;
	private final static String MAX_ID_SQL = "SELECT f.id, titre " + 
			"FROM (SELECT MAX( id ) AS id FROM FILM )f INNER JOIN FILM f2 ON f2.id = f.id";
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Test
	public void findAllFilms() throws Exception {
		Film film = new Film();
		film.setTitre("L'HOMME SANS PASSE");
		//MockHttpServletRequestBuilder mm = MockMvcRequestBuilders.get("/dvdtheque/films").contentType(MediaType.APPLICATION_JSON);
		//mvc.perform(mm).andDo(MockMvcResultHandlers.print());
		//mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/films").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/films").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].titre", Is.is(film.getTitre())));
		assertNotNull(resultActions);
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
		
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/dvdtheque/films/byId/"+film.getId()).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.titre", Is.is(film.getTitre())));
		assertNotNull(resultActions);
		
	}
}
