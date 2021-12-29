package fr.fredos.dvdtheque.dvdtheque.batch.app;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = {"fr.fredos.dvdtheque.batch"})
public class BatchApplicationApp {
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext =
                SpringApplication.run(BatchApplicationApp.class, args);
		System.exit(SpringApplication.exit(applicationContext));
    }
}