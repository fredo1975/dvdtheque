package fr.fredos.dvdtheque.batch.app;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = {"fr.fredos.dvdtheque"})
public class BatchApplicationPack {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BatchApplicationPack.class, args);
    }
}
