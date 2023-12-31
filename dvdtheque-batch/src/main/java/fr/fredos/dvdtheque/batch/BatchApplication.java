package fr.fredos.dvdtheque.batch;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import jakarta.jms.Topic;


@SpringBootApplication
@EnableDiscoveryClient
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BatchApplication.class,args);
    }
	
	@Bean
	public Topic topic() {
		return new ActiveMQTopic("topic");
	}

}
