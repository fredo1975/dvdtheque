package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;

import javax.batch.runtime.BatchStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/*
@ContextConfiguration(locations = { "classpath*:test-context.xml" })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.batch.BatchApplication.class,fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})*/
public class FilmBackupTest {
	protected Logger logger = LoggerFactory.getLogger(FilmBackupTest.class);
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	/*@Autowired
	MessageChannel source;
	@Autowired
	public PollableChannel target;
	@Autowired
	public DirectChannel backupChannel;
	@Autowired
	FileReadingMessageSource fileInAdapter;*/
	//@Test
	public void launchCopyStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("copy");
		Thread.sleep(500000000);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		//fileInAdapter.start();
		//File f = (File) target.receive(10000).getPayload();
		//logger.info(f.getAbsolutePath());
		
	}
}
