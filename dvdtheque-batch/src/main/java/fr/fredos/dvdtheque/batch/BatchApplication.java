package fr.fredos.dvdtheque.batch;

import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@SpringBootApplication
@EnableDiscoveryClient
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		/*
		ConfigurableApplicationContext applicationContext =
                SpringApplication.run(BatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
		*/
		SpringApplication.run(BatchApplication.class,args);
    }
	
	@Bean
	public Topic topic() {
		return new ActiveMQTopic("topic");
	}

	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
}
