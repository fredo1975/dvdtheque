package fr.fredos.dvdtheque.batch;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.batch.configuration.BatchExportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.configuration.OAuthClientCredentialsRestTemplateInterceptor;
import fr.fredos.dvdtheque.batch.film.writer.ExcelStreamFilmWriter;
import fr.fredos.dvdtheque.batch.model.Film;

@SpringBatchTest
@ContextConfiguration(classes = {BatchExportFilmsConfiguration.class,ExcelStreamFilmWriter.class,OAuth2ClientConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {"dvdtheque-service.url=http://fake/dvdtheque-service","dvdtheque-service.films=/films/","spring.batch.job.enabled=false"})
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
	@Qualifier(value = "runExportFilmsJob")
	Job										runExportFilmsJob;
	@Autowired
    Environment 							environment;
	@Autowired
	RestTemplate 							oAuthRestTemplate;
	
	
	private void initailizeJobLauncherTestUtils() {
	    this.jobLauncherTestUtils = new JobLauncherTestUtils();
	    this.jobLauncherTestUtils.setJob(runExportFilmsJob);
	}

	@BeforeEach
	public void setUp() throws Exception {
	    this.initailizeJobLauncherTestUtils();
	    mockServer = MockRestServiceServer.createServer(oAuthRestTemplate);
	}
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
		
        final JobExecution result = jobLauncherTestUtils.getJobLauncher().run(runExportFilmsJob, jobLauncherTestUtils.getUniqueJobParameters());
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
