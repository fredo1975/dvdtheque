package fr.fredos.dvdtheque.batch;

import org.mockito.Mockito;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BatchTestConfiguration {
	@Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
	@Bean
    RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
}
