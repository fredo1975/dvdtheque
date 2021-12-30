package fr.fredos.dvdtheque.websocket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.rest.dao.domain.Film;

@Controller
@ComponentScan({ "fr.fredos.dvdtheque.websocket" })
@Profile({"local1","local2","dev1","dev2","prod1","prod2"})
public class DvdthequeWebSocketController {
	protected Logger logger = LoggerFactory.getLogger(DvdthequeWebSocketController.class);
	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/dvdtheque-service")
	public void onReceiveMessage(JmsStatusMessage<Film> jmsStatusMessage) {
		logger.info("onReceiveMessage jmsStatusMessage="+jmsStatusMessage.toString()); 
		this.simpMessagingTemplate.convertAndSend("/topic", jmsStatusMessage);
	}
	@SubscribeMapping("/topic")
	public void onSendMessage(){
		logger.info("onSendMessage"); 
	}
	@MessageExceptionHandler
	public String handleException(Throwable exception) {
		simpMessagingTemplate.convertAndSend("/errors", exception.getMessage());
		return exception.getMessage();
	}
}
