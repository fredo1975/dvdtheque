package fr.fredos.dvdtheque.batch.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import jakarta.jms.Topic;

@Configuration
public class JmsMessageSender {
	protected Logger logger = LoggerFactory.getLogger(JmsMessageSender.class);
    
	private JmsTemplate jmsTemplate;
	@Autowired
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	private Topic topic;
	@Autowired
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	public void sendMessage(JmsStatusMessage<Film> jmsStatusMessage) { 
		logger.info("sendMessage jmsStatusMessage="+jmsStatusMessage.toString());
		jmsTemplate.convertAndSend(topic, jmsStatusMessage);
	}
}
