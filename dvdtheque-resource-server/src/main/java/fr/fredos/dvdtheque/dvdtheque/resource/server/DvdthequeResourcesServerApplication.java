package fr.fredos.dvdtheque.dvdtheque.resource.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DvdthequeResourcesServerApplication{
	//private static final Logger LOG = LoggerFactory.getLogger(DvdthequeResourcesServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DvdthequeResourcesServerApplication.class, args);
	}
}
