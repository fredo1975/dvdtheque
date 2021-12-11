package fr.fredos.dvdtheque.allocine.service;

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.hazelcast.config.AutoDetectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AllocineScrapingServiceTest.HazelcastConfiguration.class})
@ActiveProfiles("test")
//@DataJpaTest
public class AllocineScrapingServiceTest {
	protected Logger logger = LoggerFactory.getLogger(AllocineScrapingServiceTest.class);
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
    }
}
