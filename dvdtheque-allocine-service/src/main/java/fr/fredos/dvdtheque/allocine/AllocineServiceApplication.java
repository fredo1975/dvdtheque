package fr.fredos.dvdtheque.allocine;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class AllocineServiceApplication {
	public static void main(String[] args){
        SpringApplication.run(AllocineServiceApplication.class,args);
    }
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
