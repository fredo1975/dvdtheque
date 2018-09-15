package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RippedFilmTest {
	protected Logger logger = LoggerFactory.getLogger(RippedFilmTest.class);

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils2;

	@Test
	public void launchCleanRippedFilmDBStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils2.launchStep("cleanRippedFilmDB");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	
	@Test
	@Ignore
	public void launchSaveRippedFilmStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils2.launchStep("saveRippedFilm");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	@Ignore
	public void launchJob() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils2.launchJob();
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
