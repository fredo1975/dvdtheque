package fr.fredos.dvdtheque.websocket.conf;

import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

public class SocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	/*
	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages.simpDestMatchers("/app/**").authenticated().anyMessage().authenticated();
	}
	*/
	/*
	@Override
	  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
	    messages.simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT, SimpMessageType.HEARTBEAT).permitAll()
	    .simpDestMatchers("/app/**", "/topic/**").authenticated().simpSubscribeDestMatchers("/topic/**").permitAll();
	  }

	  @Override
	  protected boolean sameOriginDisabled() {
	    return true;
	  }*/
}
