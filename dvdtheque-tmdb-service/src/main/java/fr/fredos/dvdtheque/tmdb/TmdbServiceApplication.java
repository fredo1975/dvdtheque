package fr.fredos.dvdtheque.tmdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TmdbServiceApplication {
	public static void main(String... args){
        SpringApplication.run(TmdbServiceApplication.class,args);
    }
	
}
