package fr.fredos.dvdtheque.websocket.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@ComponentScan({"fr.fredos.dvdtheque"})
@Profile({"local1","local2","dev1","dev2","prod"})
public class DvdthequeWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	@Value("${stomp.relay.host}")
	private String stompRelayHost;
	@Value("${stomp.relay.port}")
	private String stompRelayPort;
	@Value("${spring.activemq.user}")
	private String activemqUser;
	@Value("${spring.activemq.password}")
	private String activemqPasswd;
	@Value("${stomp.endpoint}")
	private String stompEndpoint;
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableStompBrokerRelay("/queue/", "/topic/").setRelayHost(stompRelayHost).setRelayPort(Integer.valueOf(stompRelayPort).intValue()).setClientLogin(activemqUser)
		.setClientPasscode(activemqPasswd);
		config.setApplicationDestinationPrefixes("/app");
		config.setPreservePublishOrder(true);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(stompEndpoint).setAllowedOrigins("*").withSockJS();
	}
}
