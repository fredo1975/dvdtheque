package fr.fredos.dvdtheque.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque")
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BatchApplication.class, args);
    }
}
