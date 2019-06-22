package fr.fredos.dvdtheque.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerWithJobCallTest {
	protected Logger logger = LoggerFactory.getLogger(FilmControllerWithJobCallTest.class);
	@Autowired
	private MockMvc mvc;
	
	private static final String EXPORT_FILM_LIST_URI = "/dvdtheque/films/export";
	@Test
	public void testexportFilmList() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(EXPORT_FILM_LIST_URI);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}
}
