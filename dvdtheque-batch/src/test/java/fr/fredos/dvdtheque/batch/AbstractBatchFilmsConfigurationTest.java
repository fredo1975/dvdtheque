package fr.fredos.dvdtheque.batch;

import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public abstract class AbstractBatchFilmsConfigurationTest {
	
	@Autowired
	JobLauncher 							jobLauncher;
	@Autowired
	JobRepository 							jobRepository;
	//JobLauncherTestUtils 					jobLauncherTestUtils;
	@Autowired
	ObjectMapper 							mapper;
	@Autowired
    RestTemplate							oAuthRestTemplate;
	@Autowired
    Environment 							environment;
	MockRestServiceServer 					mockServer;
    JobLauncherTestUtils 					jobLauncherTestUtils;
	@Bean
	protected JobLauncherTestUtils jobLauncherTestUtils(Job job) throws NoSuchJobException {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
		jobLauncherTestUtils.setJobRepository(jobRepository);
		return jobLauncherTestUtils;
	}
	
	
}
