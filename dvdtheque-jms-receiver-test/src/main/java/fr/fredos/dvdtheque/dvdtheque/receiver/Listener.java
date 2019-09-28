package fr.fredos.dvdtheque.dvdtheque.receiver;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.dao.model.object.Film;

@Component
public class Listener {
	protected Logger logger = LoggerFactory.getLogger(Listener.class);
	@JmsListener(destination = "topic")
	public void receiveMessage(final Message jsonMessage) throws JMSException {
		String messageData = null;
		if (jsonMessage instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) jsonMessage;
			messageData = textMessage.getText();
			JmsStatusMessage<Film> jmsStatusMessage = new Gson().fromJson(messageData, JmsStatusMessage.class);
			logger.info(jmsStatusMessage.toString());
		}
	}
}
