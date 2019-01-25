package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring-batch-job.xml",
		"classpath*:applicationContext-batch.xml",
		"classpath*:test-context.xml" })
public class TMDBTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void launchTmdbStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("tmdb");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
