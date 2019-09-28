package fr.fredos.dvdtheque.jms.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.dao.model.object.Film;

@EnableBinding(Source.class)
@Component
public class MessagePublisher {
	protected Logger logger = LoggerFactory.getLogger(MessagePublisher.class);
	@Autowired
	protected Source source;
	
	public void sendMessage(JmsStatusMessage<Film> jmsStatusMessage){
		source.output().send(MessageBuilder.withPayload(jmsStatusMessage).build());
	}
}
