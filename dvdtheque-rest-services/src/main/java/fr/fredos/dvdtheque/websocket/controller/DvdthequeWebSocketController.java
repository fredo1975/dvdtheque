package fr.fredos.dvdtheque.websocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.dao.model.object.Film;

@Controller
@ComponentScan({"fr.fredos.dvdtheque"})
public class DvdthequeWebSocketController {

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;
	
	@MessageMapping("/send")
	public void onReceiveMessage(JmsStatusMessage<Film> jmsStatusMessage) {
		this.simpMessagingTemplate.convertAndSend("topic",jmsStatusMessage);
	}
}
