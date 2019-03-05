package fr.fredos.dvdtheque.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = {"fr.fredos.dvdtheque.batch",
		"fr.fredos.dvdtheque.service",
		"fr.fredos.dvdtheque.dao",
		"fr.fredos.dvdtheque.tmdb.service"})
public class BatchApplication {
	@Autowired
    Environment environment;
	protected Logger logger = LoggerFactory.getLogger(BatchApplication.class);
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BatchApplication.class, args);
    }
}
