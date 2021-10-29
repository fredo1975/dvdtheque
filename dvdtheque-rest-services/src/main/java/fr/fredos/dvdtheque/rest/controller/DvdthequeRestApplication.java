package fr.fredos.dvdtheque.rest.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque.rest")
@EnableDiscoveryClient
public class DvdthequeRestApplication {
	public static void main(String... args) {
		SpringApplication.run(DvdthequeRestApplication.class, args);
	}
}
