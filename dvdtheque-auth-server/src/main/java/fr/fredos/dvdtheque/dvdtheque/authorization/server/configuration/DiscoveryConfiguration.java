package fr.fredos.dvdtheque.dvdtheque.authorization.server.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty(name = "eureka.enabled")
public class DiscoveryConfiguration {

}
