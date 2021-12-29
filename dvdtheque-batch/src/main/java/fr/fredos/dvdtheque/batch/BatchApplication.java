package fr.fredos.dvdtheque.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;
//@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext =
                SpringApplication.run(BatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
    }
	
	@Bean
	@Lazy(true)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
