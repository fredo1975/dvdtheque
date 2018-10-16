package fr.fredos.dvdtheque.web.controller;

import static org.junit.Assert.assertNotNull;

import java.nio.charset.Charset;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.fredos.dvdtheque.dao.model.object.Film;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class FilmControllerTest {
	@Autowired
	private MockMvc mvc;
	
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
}
