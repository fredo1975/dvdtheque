package fr.fredos.dvdtheque.jms.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"fr.fredos.dvdtheque"})
public class DvdthequeJmsPublisher {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(DvdthequeJmsPublisher.class, args);
    }
}
