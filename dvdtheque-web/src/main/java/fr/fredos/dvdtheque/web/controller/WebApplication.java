package fr.fredos.dvdtheque.web.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque")
@PropertySource("classpath:application.properties")
//@EnableAutoConfiguration
public class WebApplication {

	public static void main(String... args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
