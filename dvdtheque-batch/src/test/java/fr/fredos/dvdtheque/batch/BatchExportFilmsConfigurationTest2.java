package fr.fredos.dvdtheque.batch;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.batch.configuration.BatchExportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.film.writer.ExcelStreamFilmWriter;
import fr.fredos.dvdtheque.batch.model.Film;


@SpringBatchTest
@ContextConfiguration(classes = {BatchExportFilmsConfiguration.class,BatchApplication.class,ExcelStreamFilmWriter.class})
//@RestClientTest
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, 
	  DirtiesContextTestExecutionListener.class})
	@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class BatchExportFilmsConfigurationTest2 {
	/*
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfigurationTest2.class);
	@Autowired
    JobLauncherTestUtils 					jobLauncherTestUtils;
    MockRestServiceServer 					mockServer;
    @Autowired
	ObjectMapper 							mapper;
	@Autowired
	Job										exportFilmsJob;
	@Autowired
    RestTemplate 							restTemplate;
	@Autowired
    Environment 							environment;
    @Test
    public void testEntireJob() throws Exception {
    	logger.info("##### testEntireJob");
    	
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
        Assert.assertEquals(BatchStatus.COMPLETED, result.getStatus());
    }*/
/*
    @Test
    public void testSpecificStep() {
        Assert.assertEquals(BatchStatus.COMPLETED, jobLauncherTestUtils.launchStep("taskletStep").getStatus());
    }*/
}
