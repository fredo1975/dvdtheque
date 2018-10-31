package fr.fredos.dvdtheque.dao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque.dao")
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
