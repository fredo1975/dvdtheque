package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.batch.configuration.BatchConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BatchConfigurationTest.BatchTestConfig.class })
public class BatchConfigurationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
/*
	@Test
	public void launchCleanDBStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("cleanDB");
	}*/
	@Test
	@Ignore
	public void launchJob() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Configuration
	@Import({ BatchConfiguration.class, BatchApplication.class })
	static class BatchTestConfig {

		@Autowired
		private Job importFilmJob;

		@Bean
		JobLauncherTestUtils jobLauncherTestUtils() throws NoSuchJobException {
			JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
			jobLauncherTestUtils.setJob(importFilmJob);
			return jobLauncherTestUtils;
		}
	}
}
