package fr.fredos.dvdtheque.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque")
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BatchApplication.class, args);
    }
}
