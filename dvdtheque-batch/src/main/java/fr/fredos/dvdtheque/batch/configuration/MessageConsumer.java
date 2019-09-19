package fr.fredos.dvdtheque.batch.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@EnableJms
public class MessageConsumer {
	protected Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
	@JmsListener(destination = "dvdtheque-topic")
    public void listener(String message){
        logger.info("Message received {} ", message);
    }
}
