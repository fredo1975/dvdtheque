package fr.fredos.dvdtheque.batch.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@Component
@EnableJms*/
public class MessageConsumer {
	protected Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
	/*@JmsListener(destination = "topic")
    public void listener(String message){
        logger.info("Message received {} ", message);
    }*/
}
