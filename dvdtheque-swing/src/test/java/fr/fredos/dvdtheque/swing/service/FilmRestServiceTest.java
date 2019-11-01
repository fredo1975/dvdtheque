package fr.fredos.dvdtheque.swing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.fredos.dvdtheque.dao.model.object.Film;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { fr.fredos.dvdtheque.swing.service.FilmRestService.class })
@AutoConfigureMockMvc
public class FilmRestServiceTest {
	@MockBean
	private FilmRestService filmRestService;
	private ObjectMapper mapper = new ObjectMapper();

	private MockRestServiceServer mockServer;
	private RestTemplate restTemplate = new RestTemplate();

	@Before
	public void setup() {
		this.mockServer = MockRestServiceServer.bindTo(this.restTemplate).ignoreExpectOrder(true).build();
	}

	@Test
	public void getAllFilmsWhenResultIsSuccessShouldReturnAllFilms() throws Exception {
		List<Film> allFilms = new ArrayList<Film>();
		Film f = new Film(1l);
		allFilms.add(f);
		given(this.filmRestService.findAllFilms()).willReturn(allFilms);
		this.mockServer.expect(ExpectedCount.once(), requestTo("/films")).andExpect(method(HttpMethod.GET)).andRespond(
				withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(allFilms)));
		List<Film> films = this.filmRestService.findAllFilms();
		assertThat(films).isEqualTo(allFilms);
	}
}
