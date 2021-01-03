package fr.fredos.dvdtheque.dvdtheque.resource.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class DvdthequeResourceServerApplication{
	public static void main(String[] args) {
		SpringApplication.run(DvdthequeResourceServerApplication.class, args);
	}
/*
	@GetMapping("/account")
	public String getAccount() {
		return "account 1";
	}

	@Bean
	public ResourceServerTokenServices tokenService() {
		RemoteTokenServices tokenServices = new RemoteTokenServices();
		tokenServices.setClientId("b");
		tokenServices.setClientSecret("b");
		tokenServices.setCheckTokenEndpointUrl("http://localhost:9999/oauth/check_token");
		return tokenServices;
	}*/
}
