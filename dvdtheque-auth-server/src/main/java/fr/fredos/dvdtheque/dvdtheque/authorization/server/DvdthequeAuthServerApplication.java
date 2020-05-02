package fr.fredos.dvdtheque.dvdtheque.authorization.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAuthorizationServer
public class DvdthequeAuthServerApplication {
	public static void main(String[] args) {
        SpringApplication.run(DvdthequeAuthServerApplication.class, args);
    }
}
