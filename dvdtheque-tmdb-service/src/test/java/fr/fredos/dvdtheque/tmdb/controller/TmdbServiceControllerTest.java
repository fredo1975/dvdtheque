package fr.fredos.dvdtheque.tmdb.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.List;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.common.tmdb.model.Credits;
import fr.fredos.dvdtheque.common.tmdb.model.Crew;
import fr.fredos.dvdtheque.common.tmdb.model.ReleaseDates;
import fr.fredos.dvdtheque.common.tmdb.model.ReleaseDatesResults;
import fr.fredos.dvdtheque.common.tmdb.model.ReleaseDatesResultsValues;
import fr.fredos.dvdtheque.common.tmdb.model.Results;
import fr.fredos.dvdtheque.common.tmdb.model.SearchResults;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ContextConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TmdbServiceControllerTest {
	
	protected Logger 								logger = LoggerFactory.getLogger(TmdbServiceControllerTest.class);
	private static final String 					TMDB_BASE_URI = "/dvdtheque-tmdb-service/";
	private static final String 					SEARCH_BY_TMDB_ID = TMDB_BASE_URI + "/retrieveTmdbFilm/byTmdbId";
	private static final String 					SEARCH_REL_DATE_BY_TMDB_ID = TMDB_BASE_URI + "/retrieveTmdbFrReleaseDate/byTmdbId";
	private static final String 					SEARCH_CREDITS_BY_TMDB_ID = TMDB_BASE_URI + "/retrieveTmdbCredits/byTmdbId";
	private static final String 					SEARCH_RESULTS_BY_TITLE = TMDB_BASE_URI + "/retrieveTmdbFilmListByTitle/byTitle";
	private static final String 					SEARCH_RESULTS_BY_TITLE_BY_PAGE = TMDB_BASE_URI + "/retrieveTmdbSearchResultsByTitle/byTitle";
	private static final String 					PROFILE_IMAGE_EXISTS_BY_POSTER_PATH = TMDB_BASE_URI + "/checkIfProfileImageExists/byPosterPath";
	private static final String 					POSTER_IMAGE_EXISTS_BY_POSTER_PATH = TMDB_BASE_URI + "/checkIfPosterExists/byPosterPath";
	private static final String 					TITLE_300 = "300";
	private static final String 					POSTER_PATH_CRUELLA = "http://image.tmdb.org/t/p/w500/iXZUPOlWwifW73ObGoSCvZ5qVSQ.jpg";
	private static final String 					POSTER_PATH_EMMA_STONE = "http://image.tmdb.org/t/p/w500/2hwXbPW2ffnXUe1Um0WXHG0cTwb.jpg";
	public static final MediaType 					APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private MockRestServiceServer 					mockServer;
	public static Long 								TMDB_ID_1271 = Long.valueOf(1271);
	public static final String 						TITRE_FILM_TMBD_ID_1271 = "300";
	public static final String 						RELEASE_DATE_FILM_TMBD_ID_1271 = "2007-03-07";
	public static final String 						POSTER_PATH_FILM_TMBD_ID_1271 = "/q31SmDy9UvSPIuTz65XsHuPwhuS.jpg";
	@Autowired
	private MockMvc 								mockmvc;
	@MockBean
	private JwtDecoder 								jwtDecoder;
	@Autowired
	private RestTemplate 							restTemplate;
	@Autowired
	private Environment 							environment;
	@Autowired
	private ObjectMapper 							mapper;
	@BeforeEach()
	public void setUp() throws Exception {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}
	
	private Results buildResults() {
		Results results = new Results();
		results.setId(TMDB_ID_1271);
		results.setPoster_path(POSTER_PATH_FILM_TMBD_ID_1271);
		results.setTitle(TITRE_FILM_TMBD_ID_1271);
		results.setRelease_date(RELEASE_DATE_FILM_TMBD_ID_1271);
		results.setOriginal_title(TITRE_FILM_TMBD_ID_1271);
		return results;
	}
	@WithMockUser(roles = "use")
	@Test
	public void retrieveTmdbFilm() throws Exception {
		
		Results results = buildResults();
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(TmdbServiceController.TMDB_MOVIE_QUERY)+Long.toString(TMDB_ID_1271)+"?"+"api_key="+environment.getRequiredProperty(TmdbServiceController.TMDB_API_KEY)+"&language=fr"))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(results), MediaType.APPLICATION_JSON));
		
		mockmvc.perform((MockMvcRequestBuilders.get(SEARCH_BY_TMDB_ID)
				.with(csrf()).param("tmdbId", Long.toString(TMDB_ID_1271)))).andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.poster_path", Is.is("/q31SmDy9UvSPIuTz65XsHuPwhuS.jpg")))
		.andExpect(MockMvcResultMatchers.jsonPath("$.original_title", Is.is("300")))
		.andExpect(MockMvcResultMatchers.jsonPath("$.release_date", Is.is("2007-03-07")))
		.andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("300")));
	}
	@WithMockUser(roles = "user")
	@Test
	public void retrieveTmdbFrReleaseDate() throws Exception {
		
		ReleaseDates relDates = new ReleaseDates();
		ReleaseDatesResults relDatesResults = new ReleaseDatesResults();
		ReleaseDatesResultsValues val = new ReleaseDatesResultsValues();
		val.setRelease_date(RELEASE_DATE_FILM_TMBD_ID_1271);
		List<ReleaseDatesResultsValues> l = List.of(val);
		relDatesResults.setRelease_dates(l);
		relDatesResults.setIso_3166_1("FR");
		List<ReleaseDatesResults> ll = List.of(relDatesResults);
		relDates.setResults(ll);
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(TmdbServiceController.TMDB_MOVIE_QUERY)+Long.toString(TMDB_ID_1271)+"/release_dates?api_key="+environment.getRequiredProperty(TmdbServiceController.TMDB_API_KEY)))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(relDates), MediaType.APPLICATION_JSON));
		
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_REL_DATE_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1271)))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is("2007-03-06T23:00:00.000+00:00")));
	}
	@WithMockUser(roles = "user")
	@Test
	public void retrieveTmdbCredits() throws Exception{
		Credits credits = new Credits();
		credits.setId(TMDB_ID_1271);
		Crew crew = new Crew();
		crew.setName("William Hoy");
		crew.setCredit_id("52fe42ecc3a36847f802d26d");
		List<Crew> l = List.of(crew);
		credits.setCrew(l);
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(TmdbServiceController.TMDB_MOVIE_QUERY)+Long.toString(TMDB_ID_1271)+"/credits?api_key="+environment.getRequiredProperty(TmdbServiceController.TMDB_API_KEY)))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(credits), MediaType.APPLICATION_JSON));
		
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_CREDITS_BY_TMDB_ID).param("tmdbId", Long.toString(TMDB_ID_1271)))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.crew[0].credit_id", Is.is("52fe42ecc3a36847f802d26d")))
		.andExpect(MockMvcResultMatchers.jsonPath("$.crew[0].name", Is.is("William Hoy")));
	}
	@WithMockUser(roles = "user")
	@Test
	public void retrieveTmdbSearchResultsByTitle() throws Exception{
		SearchResults searchResults = new SearchResults();
		searchResults.setTotal_results(1);
		searchResults.setTotal_pages(0);
		Results res = new Results();
		res.setId(TMDB_ID_1271);
		res.setTitle(TITRE_FILM_TMBD_ID_1271);
		res.setOriginal_title(TITRE_FILM_TMBD_ID_1271);
		res.setPoster_path(POSTER_PATH_FILM_TMBD_ID_1271);
		res.setRelease_date(RELEASE_DATE_FILM_TMBD_ID_1271);
		List<Results> l = List.of(res);
		searchResults.setResults(l);
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(TmdbServiceController.TMDB_SEARCH_MOVIE_QUERY)+"?"
		+"api_key="+environment.getRequiredProperty(TmdbServiceController.TMDB_API_KEY)
		+"&query="+TITLE_300
		+"&language=fr&page="+1))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(searchResults), MediaType.APPLICATION_JSON));
		
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_RESULTS_BY_TITLE_BY_PAGE).param("title", TITLE_300).param("page", String.valueOf(1)))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.results[0].id", Is.is(1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.results[0].title", Is.is(TITRE_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.results[0].original_title", Is.is(TITRE_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.results[0].poster_path", Is.is(POSTER_PATH_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.results[0].release_date", Is.is(RELEASE_DATE_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.total_pages", Is.is(0)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.total_results", Is.is(1)));
	}
	@WithMockUser(roles = "user")
	@Test
	public void retrieveTmdbFilmListByTitle() throws Exception{
		SearchResults searchResults = new SearchResults();
		searchResults.setTotal_results(1);
		searchResults.setTotal_pages(0);
		Results res = new Results();
		res.setId(TMDB_ID_1271);
		res.setTitle(TITRE_FILM_TMBD_ID_1271);
		res.setOriginal_title(TITRE_FILM_TMBD_ID_1271);
		res.setPoster_path(POSTER_PATH_FILM_TMBD_ID_1271);
		res.setRelease_date(RELEASE_DATE_FILM_TMBD_ID_1271);
		List<Results> l = List.of(res);
		searchResults.setResults(l);
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(environment.getRequiredProperty(TmdbServiceController.TMDB_SEARCH_MOVIE_QUERY)+"?"+"api_key="+environment.getRequiredProperty(TmdbServiceController.TMDB_API_KEY)+"&query="+TITLE_300+"&language=fr&page="+1))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(searchResults), MediaType.APPLICATION_JSON));
		
		
		mockmvc.perform(MockMvcRequestBuilders.get(SEARCH_RESULTS_BY_TITLE).param("title", TITLE_300))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Is.is(TITRE_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].original_title", Is.is(TITRE_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].poster_path", Is.is(POSTER_PATH_FILM_TMBD_ID_1271)))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].release_date", Is.is(RELEASE_DATE_FILM_TMBD_ID_1271)));
	}
	@WithMockUser(roles = "user")
	@Test
	public void checkIfPosterExists() throws Exception {
		byte[] imageBytes = new byte[10];
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(POSTER_PATH_CRUELLA))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(imageBytes), MediaType.APPLICATION_OCTET_STREAM));
		
		mockmvc.perform(MockMvcRequestBuilders.get(POSTER_IMAGE_EXISTS_BY_POSTER_PATH).param("posterPath", POSTER_PATH_CRUELLA))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(Boolean.TRUE)));
	}
	@WithMockUser(roles = "user")
	@Test
	public void checkIfProfileImageExists() throws Exception {
		byte[] imageBytes = new byte[10];
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(POSTER_PATH_EMMA_STONE))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(withSuccess(mapper.writeValueAsString(imageBytes), MediaType.APPLICATION_OCTET_STREAM));
		
		mockmvc.perform(MockMvcRequestBuilders.get(PROFILE_IMAGE_EXISTS_BY_POSTER_PATH).param("profilePath", POSTER_PATH_EMMA_STONE))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(Boolean.TRUE)));
	}
}
