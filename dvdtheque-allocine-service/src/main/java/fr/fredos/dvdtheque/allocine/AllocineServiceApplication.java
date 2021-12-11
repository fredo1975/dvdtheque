package fr.fredos.dvdtheque.allocine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AllocineServiceApplication {
	public static void main(String[] args){
        SpringApplication.run(AllocineServiceApplication.class,args);
    }
}
