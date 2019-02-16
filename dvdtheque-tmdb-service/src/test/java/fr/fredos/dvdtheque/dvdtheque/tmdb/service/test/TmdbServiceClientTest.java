package fr.fredos.dvdtheque.dvdtheque.tmdb.service.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@RunWith(SpringRunner.class)
@RestClientTest(TmdbServiceClientTest.class)
public class TmdbServiceClientTest {
	@Autowired
    private TmdbServiceClient client;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
	protected FilmService filmService;
	
    
}
