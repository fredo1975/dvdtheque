package fr.fredos.dvdtheque.tmdb.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "user")
public class TmdbServiceControllerTest {
	//protected Logger 							logger = LoggerFactory.getLogger(TmdbServiceControllerTest.class);
	public static final String 					TMDB_BASE_URI = "/dvdtheque-tmdb-service/";
	public static final String 					SEARCH_BY_TMDB_ID = TMDB_BASE_URI + "/retrieveTmdbFilm/byTmdbId";
	public static final String 					SEARCH_REL_DATE_BY_TMDB_ID = TMDB_BASE_URI + "/retrieveTmdbFrReleaseDate/byTmdbId";
	public static final String 					SEARCH_CREDITS_BY_TMDB_ID = TMDB_BASE_URI + "/retrieveTmdbCredits/byTmdbId";
	public static final String 					SEARCH_RESULTS_BY_TITLE = TMDB_BASE_URI + "/retrieveTmdbFilmListByTitle/byTitle";
	public static final String 					PROFILE_IMAGE_EXISTS_BY_POSTER_PATH = TMDB_BASE_URI + "/checkIfProfileImageExists/byPosterPath";
	public static final String 					POSTER_IMAGE_EXISTS_BY_POSTER_PATH = TMDB_BASE_URI + "/checkIfPosterExists/byPosterPath";
	public static final String 					TITLE_300 = "300";
	public static final String 					POSTER_PATH_CRUELLA = "http://image.tmdb.org/t/p/w500/iXZUPOlWwifW73ObGoSCvZ5qVSQ.jpg";
	public static final String 					POSTER_PATH_EMMA_STONE = "http://image.tmdb.org/t/p/w500/2hwXbPW2ffnXUe1Um0WXHG0cTwb.jpg";
	
	@Autowired
	private MockMvc 							mvc;
	public static final MediaType 				APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	public static Long 							TMDB_ID_1 = Long.valueOf(1271);
	
	@Test
	public void retrieveTmdbFilm() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(SEARCH_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(1271)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.poster_path", Is.is("/q31SmDy9UvSPIuTz65XsHuPwhuS.jpg")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.original_title", Is.is("300")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("300")));
	}
	
	@Test
	public void retrieveTmdbFrReleaseDate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(SEARCH_REL_DATE_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1)))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is("2007-03-20T23:00:00.000+00:00")));
	}
	@Test
	public void retrieveTmdbCredits() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get(SEARCH_CREDITS_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1)))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.crew[0].credit_id", Is.is("52fe42ecc3a36847f802d26d")))
		.andExpect(MockMvcResultMatchers.jsonPath("$.crew[0].name", Is.is("William Hoy")));
	}
	@Test
	public void retrieveTmdbFilmListByTitle() throws Exception{
		mvc
		.perform(MockMvcRequestBuilders.get(SEARCH_RESULTS_BY_TITLE).param("title", TITLE_300))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(2023)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Is.is("Hidalgo")))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].original_title", Is.is("Hidalgo")))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].poster_path", Is.is("/3HHzAfV09miyaKrPwSQK1g6edka.jpg")))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].release_date", Is.is("2004-02-04")));
	}
	@Test
	public void checkIfPosterExists() throws Exception {
		mvc
		.perform(MockMvcRequestBuilders.get(POSTER_IMAGE_EXISTS_BY_POSTER_PATH).param("posterPath", POSTER_PATH_CRUELLA))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(Boolean.TRUE)));
	}
	@Test
	public void checkIfProfileImageExists() throws Exception {
		mvc
		.perform(MockMvcRequestBuilders.get(PROFILE_IMAGE_EXISTS_BY_POSTER_PATH).param("profilePath", POSTER_PATH_EMMA_STONE))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(Boolean.TRUE)));
	}
}
