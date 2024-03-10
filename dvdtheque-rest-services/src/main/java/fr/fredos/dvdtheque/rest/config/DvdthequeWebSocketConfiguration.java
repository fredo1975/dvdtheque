package fr.fredos.dvdtheque.rest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
@Profile({ "local1", "local2", "dev1", "dev2", "prod1", "prod2" })
public class DvdthequeWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	protected Logger logger = LoggerFactory.getLogger(DvdthequeWebSocketConfiguration.class);
	
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
		config.enableStompBrokerRelay("/queue/", "/topic/").setRelayHost(stompRelayHost)
				.setRelayPort(Integer.valueOf(stompRelayPort).intValue()).setClientLogin(activemqUser)
				.setClientPasscode(activemqPasswd);
		config.setApplicationDestinationPrefixes("/app");
		config.setPreservePublishOrder(true);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(stompEndpoint)
		.setAllowedOriginPatterns("*")
		.withSockJS()
		.setWebSocketEnabled(true);
	}
	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
	    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
	    container.setMaxTextMessageBufferSize(8192);
	    container.setMaxBinaryMessageBufferSize(8192);
	    return container;
	}
	
	
}
