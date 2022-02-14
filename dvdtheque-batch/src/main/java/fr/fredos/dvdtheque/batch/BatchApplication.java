package fr.fredos.dvdtheque.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		/*
		ConfigurableApplicationContext applicationContext =
                SpringApplication.run(BatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
		*/
		SpringApplication.run(BatchApplication.class,args);
    }
	
	
}
