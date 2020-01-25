package fr.fredos.dvdtheque.dvdtheque.allocine.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import fr.fredos.dvdtheque.dao.model.object.CritiquesPresse;
import fr.fredos.dvdtheque.service.IFilmService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.allocine.service.AllocineServiceApplication.class})
@ActiveProfiles("local")
public class AllocineServiceClientTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceClientTest.class);
	private static final String TITRE = "avatar";
	@Autowired
    private AllocineServiceClient client;
    @Autowired
	protected IFilmService filmService;
    
    @Before()
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
	}
    
    private void assertSearchMovieFeedResultsIsNotNull(SearchResults res) {
		assertNotNull(res);
		assertNotNull(res.getFeed());
		assertNotNull(res.getFeed().getMovie());
		assertTrue(CollectionUtils.isNotEmpty(res.getFeed().getMovie()));
		assertNotNull(res.getFeed().getMovie().get(0));
		assertNotNull(res.getFeed().getMovie().get(0).getCode());
	}
    
    private void assertSearchReviewFeedResultsIsNotNull(SearchResults res) {
		assertNotNull(res);
		assertNotNull(res.getFeed());
		assertNotNull(res.getFeed().getReview());
		assertTrue(CollectionUtils.isNotEmpty(res.getFeed().getReview()));
		assertNotNull(res.getFeed().getReview().get(0));
		assertNotNull(res.getFeed().getReview().get(0).getNewsSource());
		assertNotNull(res.getFeed().getReview().get(0).getNewsSource().getCode());
	}
    
    @Test
    public void retrieveAllocineMovieFeedByTitleTest() {
    	SearchResults searchResults = client.retrieveAllocineMovieFeedByTitle(TITRE);
    	assertSearchMovieFeedResultsIsNotNull(searchResults);
		logger.info("allocine feed = "+searchResults.getFeed());
    }
    
    @Test
    public void retrieveAllocineReviewFeedByCodeTest() {
    	SearchResults searchResults = client.retrieveAllocineMovieFeedByTitle(TITRE);
    	assertSearchMovieFeedResultsIsNotNull(searchResults);
    	int code = searchResults.getFeed().getMovie().get(0).getCode();
    	SearchResults searchReviewFeedResults = client.retrieveAllocineReviewFeedByCode(code,1);
    	assertSearchReviewFeedResultsIsNotNull(searchReviewFeedResults);
    	logger.info("allocine feed = "+searchResults.getFeed());
    }
    
    @Test
    public void retrieveReviewListToCritiquesPresseListTest() {
    	List<CritiquesPresse> critiquesPresseList = client.retrieveReviewListToCritiquesPresseList(TITRE);
    	assertTrue(CollectionUtils.isNotEmpty(critiquesPresseList));
    }
}
