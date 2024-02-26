package fr.fredos.dvdtheque.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class TestWebSocketConfig implements WebSocketMessageBrokerConfigurer{
	private static final String STOMP_ENDPOINT = 	"dvdtheque-ws";
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/"+STOMP_ENDPOINT)
		.setAllowedOriginPatterns("*")
		.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/queue/","/topic/");
		registry.setApplicationDestinationPrefixes("/app");
	}
}
