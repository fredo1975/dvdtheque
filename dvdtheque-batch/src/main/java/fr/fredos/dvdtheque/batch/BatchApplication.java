package fr.fredos.dvdtheque.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = {"fr.fredos.dvdtheque.batch",
		"fr.fredos.dvdtheque.service",
		"fr.fredos.dvdtheque.dao",
		"fr.fredos.dvdtheque.tmdb.service",
		"fr.fredos.dvdtheque.allocine.service",
		"fr.fredos.dvdtheque.jms.publisher"})
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext =
                SpringApplication.run(BatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
    }
}
