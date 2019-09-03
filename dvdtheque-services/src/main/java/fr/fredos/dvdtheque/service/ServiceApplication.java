package fr.fredos.dvdtheque.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque.service")
@EnableCaching
public class ServiceApplication {
	
}
