package fr.dvdtheque.console;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
@ContextConfiguration(locations={"classpath:swing-applicationContext-test.xml"})
@DirtiesContext
public class TestBarriere extends AbstractTransactionalJUnit4SpringContextTests{
	protected final Log logger = LogFactory.getLog(TestBarriere.class);
	@Autowired
	private Barriere synchro;
	
	@Test
	@Ignore
	public void testBarriere(){
		logger.info("testBarriere start");
		
		Thread t = new Thread("MonThread"){
			public void run(){
				logger.info("MonThread run");
				synchro.set();
			}
		};
		synchro.reset();
		logger.info("testBarriere apres reset() synchro.isOuverte()="+synchro.isOuverte());
		t.start();
		synchro.waitOne();
		
		logger.info("testBarriere apres waitOne() synchro.isOuverte()="+synchro.isOuverte());
		synchro.set();
		
		logger.info("testBarriere end");
	}
}
