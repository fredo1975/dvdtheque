package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;


public class BatchImportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	
	@Autowired
	public Job importFilmsJob;
	
	@Before
	public void init() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJob(importFilmsJob);
	}
	
	@Test
	//@Ignore
	public void launchCleanDBStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils(importFilmsJob).launchStep("cleanDBStep");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	//@Ignore
	public void launchJob() throws Exception {
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils(importFilmsJob).launchJob(jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		
	}
}
