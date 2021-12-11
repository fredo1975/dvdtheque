package fr.fredos.dvdtheque.allocine.integration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.config.AutoDetectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.service.AllocineScrapingService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AllocineScrapingServiceTest.HazelcastConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class AllocineScrapingServiceTest {
	protected Logger logger = LoggerFactory.getLogger(AllocineScrapingServiceTest.class);
	private static final String ALLOCINE_FIULM_ID_289301 = "289301";
	private static final String ALLOCINE_FIULM_ID_289301_TITLE = "Les Bodin's en Thaïlande";
	private static final String ALLOCINE_FIULM_ID_136316 = "136316";
	private static final String ALLOCINE_FIULM_ID_136316_TITLE = "Les Eternels";
	@Autowired
    private AllocineScrapingService allocineScrapingService;
    @TestConfiguration
	public static class HazelcastConfiguration {
		@Bean
		public HazelcastInstance hazelcastInstance() {
			Config config = new Config();
			config.getNetworkConfig().setJoin(new JoinConfig().setAutoDetectionConfig(new AutoDetectionConfig().setEnabled(false)));
			config.setInstanceName(RandomStringUtils.random(8, true, false))
					.addMapConfig(new MapConfig().setName("films"));
			return Hazelcast.newHazelcastInstance(config);
		}
	}
    
    @Test
    public void retrieveAllocineScrapingMoviesFeedTest() throws IOException {
    	allocineScrapingService.retrieveAllocineScrapingMoviesFeed();
		List<FicheFilm> allFicheFilmFromPageRetrievedFromDb = allocineScrapingService.retrieveAllFicheFilm();
		assertEquals(15,allFicheFilmFromPageRetrievedFromDb.size());
		assertEquals(ALLOCINE_FIULM_ID_289301,allFicheFilmFromPageRetrievedFromDb.get(0).getAllocineFilmId());
		assertEquals(ALLOCINE_FIULM_ID_289301_TITLE,allFicheFilmFromPageRetrievedFromDb.get(0).getTitle());
		logger.info("critique presses from {} are {}",allFicheFilmFromPageRetrievedFromDb.get(0).getTitle(),allFicheFilmFromPageRetrievedFromDb.get(0).getCritiquesPresse().toString());
		assertEquals(ALLOCINE_FIULM_ID_136316,allFicheFilmFromPageRetrievedFromDb.get(1).getAllocineFilmId());
		assertEquals(ALLOCINE_FIULM_ID_136316_TITLE,allFicheFilmFromPageRetrievedFromDb.get(1).getTitle());
    }
}
