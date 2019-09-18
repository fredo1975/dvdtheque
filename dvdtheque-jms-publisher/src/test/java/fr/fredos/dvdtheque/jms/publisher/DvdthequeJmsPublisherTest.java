package fr.fredos.dvdtheque.jms.publisher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.jms.model.JmsStatusMessage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DvdthequeJmsPublisherTest {
	@Autowired
	private Source source;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void publish() throws Exception {
		JmsStatusMessage jmsStatusMessage = new JmsStatusMessage(JmsStatus.CLEAN_DB_INIT, null);
		source.output().send(MessageBuilder.withPayload(jmsStatusMessage).build());
	}
}
