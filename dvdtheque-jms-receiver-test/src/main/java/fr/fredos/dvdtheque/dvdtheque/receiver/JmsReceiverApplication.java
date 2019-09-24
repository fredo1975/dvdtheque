package fr.fredos.dvdtheque.dvdtheque.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class JmsReceiverApplication {
	public static void main(String[] args) {
		SpringApplication.run(JmsReceiverApplication.class, args);
	}
}
