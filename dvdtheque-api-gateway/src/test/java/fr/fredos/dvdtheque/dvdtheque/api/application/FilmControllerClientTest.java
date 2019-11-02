package fr.fredos.dvdtheque.dvdtheque.api.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

//@RunWith(SpringRunner.class)
@SpringJUnitConfig(classes = {FilmControllerClient.class, RestTemplate.class})
@EnableAspectJAutoProxy
public class FilmControllerClientTest {
	@Autowired
    private FilmControllerClient filmControllerClient;

	@Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
/*
    @Before()
    public void setUp() {
    	RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }*/
    /*
    @Test
    public void getAllFilms() throws RestClientException, IOException {
    	List<Film> allFilms = new ArrayList<Film>();
		Film f = new Film(1l);
		allFilms.add(f);
		final String json = "[{\"id\":1,\"annee\":null,\"titre\":null,\"titreO\":null,\"dvd\":null,\"realisateurs\":[],\"acteurs\":[],\"ripped\":false,\"vu\":false,\"posterPath\":null,\"tmdbId\":null,\"overview\":null,\"runtime\":null,\"genres\":[],\"homepage\":null,\"alreadyInDvdtheque\":false}]";
		mockServer.expect(requestTo("http://dvdtheque-service/films"))
        .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    	List<Film> films = filmControllerClient.findAllFilms();
    	assertThat(films).isEqualTo(allFilms);
    }*/
}
