package fr.fredos.dvdtheque.config.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DvdthequeConfigClientApplication implements CommandLineRunner{
	Logger LOG = LoggerFactory.getLogger(DvdthequeConfigClientApplication.class);

	@Value("${dvdtheque.web.rest.base.url}")
	String dvdthequeRestUrl;

	public static void main(String[] args) {
		SpringApplication.run(DvdthequeConfigClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("dvdthequeRestUrl: " + dvdthequeRestUrl);
	}
}
