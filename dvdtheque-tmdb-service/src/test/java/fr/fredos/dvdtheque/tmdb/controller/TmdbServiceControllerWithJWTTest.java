package fr.fredos.dvdtheque.tmdb.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.fredos.dvdtheque.tmdb.config.RealmRoleConverter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")

public class TmdbServiceControllerWithJWTTest {
	@Autowired
	private MockMvc 								mvc;
	public static final MediaType 					APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	public static Long 								TMDB_ID_1 = Long.valueOf(1271);
	private Map<String,List<String>> 				rolesMap = Map.of("roles", Arrays.asList("user"));
	private Map<String,List<String>> 				forbiddenRolesMap = Map.of("roles", Arrays.asList("fake"));
	
	@Test
	public void retrieveTmdbFilmWithStatusOk() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(TmdbServiceControllerTest.SEARCH_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1))
				.contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwt -> jwt.claim("realm_access", rolesMap)).authorities(new RealmRoleConverter())))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(1271)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.poster_path", Is.is("/q31SmDy9UvSPIuTz65XsHuPwhuS.jpg")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.original_title", Is.is("300")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("300")));
		
	}
	@Test
	public void retrieveTmdbFrReleaseDateWithStatus401() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(TmdbServiceControllerTest.SEARCH_REL_DATE_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1)))
		.andExpect(status().is4xxClientError());
	}
	@Test
	public void retrieveTmdbCreditsWithStatusOk() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get(TmdbServiceControllerTest.SEARCH_CREDITS_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1))
		.contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwt -> jwt.claim("realm_access", rolesMap)).authorities(new RealmRoleConverter())))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.crew[0].credit_id", Is.is("52fe42ecc3a36847f802d26d")))
		.andExpect(MockMvcResultMatchers.jsonPath("$.crew[0].name", Is.is("William Hoy")));
	}
	@Test
	public void retrieveTmdbFilmListByTitleWithStatusOk() throws Exception{
		mvc
		.perform(MockMvcRequestBuilders.get(TmdbServiceControllerTest.SEARCH_RESULTS_BY_TITLE).param("title", TmdbServiceControllerTest.TITLE_300)
				.contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwt -> jwt.claim("realm_access", rolesMap)).authorities(new RealmRoleConverter())))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(2023)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Is.is("Hidalgo")))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].original_title", Is.is("Hidalgo")))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].poster_path", Is.is("/3HHzAfV09miyaKrPwSQK1g6edka.jpg")))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].release_date", Is.is("2004-02-04")));
	}
	@Test
	public void checkIfPosterExistsWithStatus500() throws Exception {
		mvc
		.perform(MockMvcRequestBuilders.get(TmdbServiceControllerTest.POSTER_IMAGE_EXISTS_BY_POSTER_PATH).param("posterPath", TmdbServiceControllerTest.POSTER_PATH_CRUELLA)
				.contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwt -> jwt.claim("realm_access", forbiddenRolesMap)).authorities(new RealmRoleConverter())))
		.andExpect(status().is5xxServerError());
	}
	@Test
	public void checkIfProfileImageExistsWithStatus500() throws Exception {
		mvc
		.perform(MockMvcRequestBuilders.get(TmdbServiceControllerTest.PROFILE_IMAGE_EXISTS_BY_POSTER_PATH).param("profilePath", TmdbServiceControllerTest.POSTER_PATH_EMMA_STONE)
				.contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwt -> jwt.claim("realm_access", forbiddenRolesMap)).authorities(new RealmRoleConverter())))
		.andExpect(status().is5xxServerError());
	}
}
