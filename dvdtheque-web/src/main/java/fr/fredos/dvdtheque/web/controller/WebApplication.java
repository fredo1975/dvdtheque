package fr.fredos.dvdtheque.web.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque.web")
public class WebApplication {

	public static void main(String... args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
