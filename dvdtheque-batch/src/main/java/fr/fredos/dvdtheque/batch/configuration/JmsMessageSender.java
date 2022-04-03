package fr.fredos.dvdtheque.batch.configuration;

import javax.jms.Topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;

@Configuration
public class JmsMessageSender {
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
		jmsTemplate.convertAndSend(topic, jmsStatusMessage);
	}
}
