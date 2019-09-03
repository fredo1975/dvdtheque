package fr.fredos.dvdtheque.rest.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque")
@EnableDiscoveryClient
public class WebApplication {

	public static void main(String... args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
