package fr.fredos.dvdtheque.batch;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.client.MockRestServiceServer;

import com.hazelcast.config.AutoDetectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
@EnableBatchProcessing
@SpringBootTest
public class BatchExportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	@BeforeEach
	public void init() {
		//jobLauncherTestUtils = new JobLauncherTestUtils();
		//jobLauncherTestUtils.setJob(exportFilmsJob);
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}
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
	public void launchExportFilmsJob() throws Exception {
		/*
		List<Film> l = new ArrayList<>();
		Film film = new Film();
		l.add(film);
		mockServer.expect(ExpectedCount.once(), 
				 requestTo(environment.getRequiredProperty(BatchExportFilmsConfiguration.DVDTHEQUE_SERVICE_URL)+environment.getRequiredProperty(BatchExportFilmsConfiguration.DVDTHEQUE_SERVICE_ALL)))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(mapper.writeValueAsString(l), MediaType.APPLICATION_JSON));
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils(exportFilmsJob).launchJob(jobParameters);
		//mockServer.verify();
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());*/
	}
}
