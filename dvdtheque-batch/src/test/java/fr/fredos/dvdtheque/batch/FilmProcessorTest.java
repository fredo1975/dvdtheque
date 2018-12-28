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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.batch.configuration.BatchConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.batch.BatchApplication.class,
		fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		BatchConfiguration.class})
public class FilmProcessorTest {
	protected Logger logger = LoggerFactory.getLogger(FilmProcessorTest.class);

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Test
	@Ignore
	public void launchCheckFilmStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("checkFilm");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	
	@Test
	public void launchtheMovieDbStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("theMovieDb");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	@Ignore
	public void launchCleanDBStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("cleanDB");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	@Ignore
	public void launchSetRippedFlagStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("setRippedFlag");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

	}
	@Test
	public void launchJob() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
