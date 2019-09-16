package fr.fredos.dvdtheque.batch.jms.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.batch.jms.model.JmsStatusMessage;

@EnableBinding(Source.class)
@Component
public class MessagePublisher {
	protected Logger logger = LoggerFactory.getLogger(MessagePublisher.class);
	@Autowired
	private Source source;
	
	public void sendMessage(JmsStatusMessage jmsStatusMessage){
		source.output().send(MessageBuilder.withPayload(jmsStatusMessage).build());
	}
}
