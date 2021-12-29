package fr.fredos.dvdtheque.batch;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.batch.configuration.BatchExportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.model.Film;


@SpringBatchTest
@ContextConfiguration(classes = {BatchApplication.class, BatchTestConfiguration.class})
public class BatchExportFilmsConfigurationTest2 {
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfigurationTest2.class);
	@Lazy
	@Autowired
    JobLauncherTestUtils 					jobLauncherTestUtils;
    MockRestServiceServer 					mockServer;
    @Autowired
	ObjectMapper 							mapper;
	@Autowired
    RestTemplate 							restTemplate;
	@Autowired
    Environment 							environment;
    @Test
    public void testEntireJob() throws Exception {
    	logger.info("##### testEntireJob");
    	/*
    	logger.info("##### testEntireJob");
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(BatchApplication.class);
    	RestTemplate restTemplate = ctx.getBean(RestTemplate.class);
    	mockServer = MockRestServiceServer.createServer(restTemplate);
    	JobLauncherTestUtils jobLauncherTestUtils = ctx.getBean(JobLauncherTestUtils.class);
    	Job exportFilmsJob= ctx.getBean(Job.class);
    	Environment environment = ctx.getBean(Environment.class);
    	*/
    	mockServer = MockRestServiceServer.createServer(restTemplate);
    	
    	/*
    	List<Film> l = new ArrayList<>();
		Film film = new Film();
		l.add(film);
		mockServer.expect(ExpectedCount.once(), 
				 requestTo(environment.getRequiredProperty(BatchExportFilmsConfiguration.DVDTHEQUE_SERVICE_URL)+environment.getRequiredProperty(BatchExportFilmsConfiguration.DVDTHEQUE_SERVICE_ALL)))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(mapper.writeValueAsString(l), MediaType.APPLICATION_JSON));
		
        final JobExecution result = jobLauncherTestUtils.getJobLauncher().run(exportFilmsJob, jobLauncherTestUtils.getUniqueJobParameters());
        mockServer.verify();
        Assert.assertNotNull(result);
        Assert.assertEquals(BatchStatus.COMPLETED, result.getStatus());*/
    }
/*
    @Test
    public void testSpecificStep() {
        Assert.assertEquals(BatchStatus.COMPLETED, jobLauncherTestUtils.launchStep("taskletStep").getStatus());
    }*/
}
