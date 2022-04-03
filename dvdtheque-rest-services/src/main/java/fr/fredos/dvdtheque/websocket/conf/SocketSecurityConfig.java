package fr.fredos.dvdtheque.websocket.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class SocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
/*
	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages.simpTypeMatchers(SimpMessageType.CONNECT,
				SimpMessageType.SUBSCRIBE,
				SimpMessageType.UNSUBSCRIBE, 
				SimpMessageType.DISCONNECT,
				SimpMessageType.HEARTBEAT).permitAll().simpDestMatchers("/app/**", "/topic/**","/dvdtheque-ws/websocket/**").permitAll()
				.simpSubscribeDestMatchers("/topic/**","/dvdtheque-ws/websocket/**").permitAll();
	}

	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}*/
	@Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().permitAll();
    }
	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}
