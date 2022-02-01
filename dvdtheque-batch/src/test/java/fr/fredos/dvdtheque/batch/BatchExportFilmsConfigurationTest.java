package fr.fredos.dvdtheque.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import fr.fredos.dvdtheque.batch.configuration.BatchExportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.model.Film;

@SpringBootTest(classes = {BatchExportFilmsConfiguration.class,OAuth2ClientConfiguration.class})
@Disabled
public class BatchExportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfigurationTest.class);
	@Autowired
	@Qualifier(value = "runExportFilmsJob")
	Job										runExportFilmsJob;
	
	@Test
	@Disabled
	public void launchExportFilmsJob() throws Exception {
		logger.info("##### testEntireJob");
		mockServer = MockRestServiceServer.createServer(oAuthRestTemplate);
		
		List<Film> l = new ArrayList<>();
		Film film = new Film();
		l.add(film);
		mockServer.expect(ExpectedCount.once(), 
				 requestTo(environment.getRequiredProperty(BatchExportFilmsConfiguration.DVDTHEQUE_SERVICE_URL)+environment.getRequiredProperty(BatchExportFilmsConfiguration.DVDTHEQUE_SERVICE_ALL)))
		          .andExpect(method(HttpMethod.GET))
		          .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(mapper.writeValueAsString(l), MediaType.APPLICATION_JSON));
		          
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils(runExportFilmsJob).launchJob(jobParameters);
		mockServer.verify();
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
