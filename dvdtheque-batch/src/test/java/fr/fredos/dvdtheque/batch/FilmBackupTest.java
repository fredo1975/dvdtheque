package fr.fredos.dvdtheque.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { /*"classpath*:spring-int-copy-files.xml",*/
		"classpath*:test-context.xml",
		"classpath*:applicationContext-batch.xml"})
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
	@Test
	public void launchCopyStep() throws Exception {
		/*
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("copy");
		Thread.sleep(500000000);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());*/
		//fileInAdapter.start();
		//File f = (File) target.receive(10000).getPayload();
		//logger.info(f.getAbsolutePath());
		
	}
}
