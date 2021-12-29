package fr.fredos.dvdtheque.batch;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBatchTest
@ContextConfiguration(classes = {BatchApplication.class, BatchTestConfiguration.class})
public class BatchExportFilmsConfigurationTest2 {
	@Autowired
    JobLauncherTestUtils 			jobLauncherTestUtils;
    @Autowired
	Job 							exportFilmsJob;
    
    
    
    @Test
    public void testEntireJob() throws Exception {
        /*final JobExecution result = jobLauncherTestUtils.getJobLauncher().run(exportFilmsJob, jobLauncherTestUtils.getUniqueJobParameters());
        Assert.assertNotNull(result);
        Assert.assertEquals(BatchStatus.COMPLETED, result.getStatus());*/
    }

    @Test
    public void testSpecificStep() {
        Assert.assertEquals(BatchStatus.COMPLETED, jobLauncherTestUtils.launchStep("taskletStep").getStatus());
    }
}
