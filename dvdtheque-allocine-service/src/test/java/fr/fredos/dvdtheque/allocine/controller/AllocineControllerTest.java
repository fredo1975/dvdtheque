package fr.fredos.dvdtheque.allocine.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.integration.config.HazelcastConfiguration;
import fr.fredos.dvdtheque.allocine.service.AllocineService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HazelcastConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AllocineControllerTest {
	protected Logger 								logger = LoggerFactory.getLogger(AllocineControllerTest.class);
	@Autowired
	private MockMvc 								mockmvc;
	@MockBean
	private JwtDecoder 								jwtDecoder;
	@MockBean
	private AllocineService 						allocineService;
	@Test
	@WithMockUser(roles = "user")
	public void getAllocineFicheFilmById() throws Exception {
		Mockito.when(allocineService.retrieveFicheFilm(org.mockito.Mockito.anyInt())).thenReturn(Optional.of(new FicheFilm()));
		mockmvc.perform(MockMvcRequestBuilders.get("/dvdtheque-allocine-service/byId").param("id", String.valueOf(0))
				.with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	@WithMockUser(roles = "user")
	public void getAllocineFicheFilmByTitle() throws Exception {
		Mockito.when(allocineService.retrieveFicheFilmByTitle(org.mockito.Mockito.anyString())).thenReturn(List.of(new FicheFilm()));
		mockmvc.perform(MockMvcRequestBuilders.get("/dvdtheque-allocine-service/byTitle").param("title", "title")
				.with(jwt().jwt(builder -> builder.subject("test")))
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
}
