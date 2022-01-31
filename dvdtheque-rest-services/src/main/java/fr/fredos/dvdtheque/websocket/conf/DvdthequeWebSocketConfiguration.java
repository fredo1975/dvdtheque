package fr.fredos.dvdtheque.websocket.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@ComponentScan({ "fr.fredos.dvdtheque" })
@Profile({ "local1", "local2", "dev1", "dev2", "prod1", "prod2" })
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
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
	public void configureClientInboundChannel(ChannelRegistration registration) {
		/*
		
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) accessor.getHeader("simpUser");
					logger.debug("keycloakAuthenticationToken: CONNECT {}", keycloakAuthenticationToken.toString());

					Optional.ofNullable(accessor.getNativeHeader("Authorization")).ifPresent(ah -> {
			            String bearerToken = ah.get(0).replace("Bearer ", "");
			            logger.debug("Received bearer token {}", bearerToken);
			          });
					
					accessor.setUser(keycloakAuthenticationToken);
				}
				return message;
			}

			@Override
			public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
					KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) accessor.getHeader("simpUser");
					logger.debug("keycloakAuthenticationToken: DISCONNECT {}", keycloakAuthenticationToken.toString());
				}
				ChannelInterceptor.super.postSend(message, channel, sent);
			}
			
			
		});
		*/
	}

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
		registry.addEndpoint(stompEndpoint).setAllowedOriginPatterns("*").withSockJS();
	}
}
