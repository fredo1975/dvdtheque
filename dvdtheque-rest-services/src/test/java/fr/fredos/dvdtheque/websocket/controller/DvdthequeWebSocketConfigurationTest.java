package fr.fredos.dvdtheque.websocket.controller;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.websocket.conf.DvdthequeWebSocketConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DvdthequeWebSocketConfiguration.class},
webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DvdthequeWebSocketConfigurationTest {
	protected Logger logger = LoggerFactory.getLogger(DvdthequeWebSocketConfigurationTest.class);
	static final String WEBSOCKET_TOPIC = "/topic";
    @Value("${local.server.port}")
    private int port;
    private String WEBSOCKET_URI;
    private CompletableFuture<String> completableFuture;
    WebSocketStompClient stompClient;
    private static final String SEND_CREATE_JMS_STATUS_ENDPOINT = "/app/send/";
    @Before
    public void setup() {
    	completableFuture = new CompletableFuture<>();
        
        WEBSOCKET_URI = "ws://localhost:" + port + "/websocket";
    }

	@Test
    public void shouldReceiveAMessageFromTheServer() throws Exception {
		stompClient = new WebSocketStompClient(new SockJsClient(Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession session = stompClient
                .connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        Thread.currentThread().sleep(1000);
        //session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());
        session.subscribe(SEND_CREATE_JMS_STATUS_ENDPOINT, new MyStompFrameHandler());
        Thread.currentThread().sleep(1000);
        JmsStatusMessage<Film> jms = new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_INIT,null);
        session.send(WEBSOCKET_TOPIC, jms);
        Thread.currentThread().sleep(1000);
        assertEquals(jms, completableFuture.get(10, TimeUnit.SECONDS));
    }

    class MyStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }
        
        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
        	logger.info("received message: {} with headers: {}", payload, stompHeaders);
        	completableFuture.complete(payload.toString());
        }
    }
}
