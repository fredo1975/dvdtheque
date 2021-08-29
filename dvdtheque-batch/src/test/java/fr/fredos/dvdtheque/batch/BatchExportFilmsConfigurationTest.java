package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.fredos.dvdtheque.batch.configuration.BatchExportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.film.tasklet.RetrieveDateInsertionTasklet;
import fr.fredos.dvdtheque.batch.film.tasklet.RippedFlagTasklet;

@SpringBootTest(classes = { BatchExportFilmsConfiguration.class,
		RippedFlagTasklet.class,
		RetrieveDateInsertionTasklet.class,
		fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class,
		fr.fredos.dvdtheque.allocine.service.AllocineServiceApplication.class})
public class BatchExportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	@Autowired
	public Job exportFilmsJob;
	
	@BeforeEach
	public void init() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJob(exportFilmsJob);
	}
	
	@Test
	public void launchExportFilmsJob() throws Exception {
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils(exportFilmsJob).launchJob(jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
