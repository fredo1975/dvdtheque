package fr.fredos.dvdtheque.rest.websocket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.hazelcast.core.Hazelcast;

import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.integration.config.HazelcastConfiguration;
import fr.fredos.dvdtheque.rest.DvdthequeRestApplication;
import fr.fredos.dvdtheque.rest.dao.domain.Film;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DvdthequeWebSocketControllerTest.TestWebSocketConfig.class,HazelcastConfiguration.class,
		DvdthequeRestApplication.class},
webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
		properties = { "eureka.client.enabled:false", "spring.cloud.config.enabled:false" })
@ActiveProfiles("test")
public class DvdthequeWebSocketControllerTest {
	protected Logger logger = LoggerFactory.getLogger(DvdthequeWebSocketControllerTest.class);
	@Value("${local.server.port}")
    private int port;
    private String WEBSOCKET_URI;
    WebSocketStompClient stompClient;
    private static final String SEND_CREATE_JMS_STATUS_ENDPOINT = "/app/";
    private static final String SUBSCRIBE_TOPIC_ENDPOINT = "/topic";
    private StompSession 							stompSession;
    private RestTemplate restTemplate;
    @MockBean
	private JwtDecoder 								jwtDecoder;
    private MockRestServiceServer 					mockServer;
    @BeforeEach
    public void setup() throws InterruptedException, ExecutionException, TimeoutException {
    	restTemplate = new RestTemplate();
    	//mockServer = MockRestServiceServer.createServer(restTemplate);
    	stompClient = new WebSocketStompClient(new SockJsClient(Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
    	stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    	String host = "localhost";
        WEBSOCKET_URI = "ws://"+host+":"+port+"/dvdtheque-ws";
        String homeUrl = "http://{host}:{port}/dvdtheque-ws";
		logger.debug("Sending warm-up HTTP request to " + homeUrl);
        HttpStatus status = restTemplate.getForEntity(homeUrl, Void.class, host, port).getStatusCode();
		//Assert.state(status == HttpStatus.OK);
        assertThat(status).isEqualTo(HttpStatus.OK);
        stompSession = stompClient.connect(WEBSOCKET_URI, new MyStompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
    }
    
    @After
    public void tearDown() throws Exception {
    	Hazelcast.shutdownAll();
    	try {
    		stompSession.disconnect();
    	}catch (Throwable t) {
			System.err.println("Failed to stop stompSession");
			t.printStackTrace();
		}
    	try {
    		stompClient.stop();
    	}catch (Throwable t) {
			System.err.println("Failed to stop stompClient");
			t.printStackTrace();
		}
    }
    /*
    @Test
    public void connectsToSocket() throws Exception {
    	assertThat(stompSession).isNotNull();
        assertThat(stompSession.isConnected()).isTrue();
    }*/
	
	@Test
	@WithMockUser(roles = "user")
    public void shouldReceiveAMessageFromTheServer() throws Exception {
        ListenableFuture<StompSession> l = stompClient.connect(WEBSOCKET_URI, new MyStompSessionHandlerAdapter() {});
        stompSession = l.get(1, TimeUnit.SECONDS);
		CompletableFuture<JmsStatusMessage<Film>> resultKeeper = new CompletableFuture<>();
        stompSession.subscribe(SUBSCRIBE_TOPIC_ENDPOINT, new MyStompFrameHandler((payload) -> resultKeeper.complete(payload)));
        JmsStatusMessage<Film> jms = new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_INIT,null,0l,JmsStatus.CLEAN_DB_INIT.statusValue());
        stompSession.send(SEND_CREATE_JMS_STATUS_ENDPOINT, jms);
        assertEquals(jms, resultKeeper.get(5, TimeUnit.SECONDS));
    }
	public static class MyStompFrameHandler implements StompFrameHandler {
    	private final Consumer<JmsStatusMessage<Film>> frameHandler;
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return JmsStatusMessage.class;
        }
        public MyStompFrameHandler(Consumer<JmsStatusMessage<Film>> frameHandler) {
            this.frameHandler = frameHandler;
        }
        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
        	frameHandler.accept((JmsStatusMessage<Film>) payload);
        }
    }
    
    class MyStompSessionHandlerAdapter extends StompSessionHandlerAdapter{
		@Override
		public Type getPayloadType(StompHeaders headers) {
			return super.getPayloadType(headers);
		}
		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			super.handleFrame(headers, payload);
		}
		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			super.afterConnected(session, connectedHeaders);
		}
		@Override
		public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
				Throwable exception) {
			super.handleException(session, command, headers, payload, exception);
		}
		@Override
		public void handleTransportError(StompSession session, Throwable exception) {
			super.handleTransportError(session, exception);
		}
    }
	@EnableWebSocketMessageBroker
	static class TestWebSocketConfig implements WebSocketMessageBrokerConfigurer{

		@Autowired
		Environment env;

		@Override
		public void registerStompEndpoints(StompEndpointRegistry registry) {
			registry.addEndpoint("/dvdtheque-ws").setAllowedOriginPatterns("*").withSockJS().setWebSocketEnabled(true);
		}

		@Override
		public void configureMessageBroker(MessageBrokerRegistry registry) {
			registry.enableSimpleBroker("/topic/");
			//registry.enableStompBrokerRelay("/queue/", "/topic/");
			registry.setApplicationDestinationPrefixes("/app", "/topic");
		}
	}
	
	@SpringBootApplication
    static class WebApplicationTest {

    	public static void main(String... args) {
    		SpringApplication.run(DvdthequeRestApplication.class, args);
    	}
    }
}
