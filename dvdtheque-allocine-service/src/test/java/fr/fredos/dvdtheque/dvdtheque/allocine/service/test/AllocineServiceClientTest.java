package fr.fredos.dvdtheque.dvdtheque.allocine.service.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.allocine.model.SearchResults;
import fr.fredos.dvdtheque.allocine.service.AllocineServiceClient;
import fr.fredos.dvdtheque.service.IFilmService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.allocine.service.AllocineServiceApplication.class})
@ActiveProfiles("local")
public class AllocineServiceClientTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceClientTest.class);
	
	@Autowired
    private AllocineServiceClient client;
    @Autowired
	protected IFilmService filmService;
    
    @Before()
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
	}
    
    private void assertSearchResultsIsNotNull(SearchResults res) {
		assertNotNull(res);
		/*
		assertNotNull(res.getId());
		assertNotNull(res.getOriginal_title());
		assertNotNull(res.getPoster_path());
		assertNotNull(res.getTitle());
		assertNotNull(res.getRelease_date());
		assertNotNull(res.getOverview());
		assertNotNull(res.getRuntime());*/
	}
    
    @Test
    public void retrieveFeedTest() {
    	final String titre = "avatar";
    	SearchResults searchResults = client.retrieveAllocineFeedByTtile(titre);
    	assertSearchResultsIsNotNull(searchResults);
		logger.info("allocine feed = "+searchResults.getFeed());
    }
}
