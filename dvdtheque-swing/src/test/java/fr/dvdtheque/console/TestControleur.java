package fr.dvdtheque.console;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import fr.dvdtheque.console.controleur.Controleur;
@ContextConfiguration(locations={"classpath:swing-applicationContext-test.xml"})
@DirtiesContext
public class TestControleur extends AbstractTransactionalJUnit4SpringContextTests {
	protected final Log logger = LogFactory.getLog(TestControleur.class);
	@Autowired
	private Controleur controleur;
	@Test
	public void testControleur(){
		logger.info("testControleur start");
		//controleur.run();
		logger.info("testControleur end");
	}
}
