package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.dao.model.repository.AuthenticatorDao;

@ContextConfiguration(locations={"classpath:business-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticatorServiceTests extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorServiceTests.class);
	@Autowired
	protected AuthenticatorDao authenticatorDao;
	
	@Autowired
	protected AuthenticatorService authenticatorService;
	
	@Test
	public void tryAuthenticatorSuccessLogin() throws Exception{
		logger.info("tryAuthenticatorSuccessLogin start");
		User existingUser = authenticatorDao.authenticate("fredo", "fredo");
		assertNotNull(existingUser);
		logger.info(existingUser.toString());
		
		logger.info("tryAuthenticatorSuccessLogin end");
	}
	@Test
	public void tryAuthenticatorDaoFailedLogin(){
		logger.info("tryAuthenticatorDaoFailedLogin start");
		User nonExistingUser = authenticatorDao.authenticate("sdsd", "sdsd");
		assertNull(nonExistingUser);
		logger.info("tryAuthenticatorDaoFailedLogin end");
	}
	@Test
	public void tryAuthenticatorServiceFailedLogin(){
		logger.info("tryAuthenticatorServiceFailedLogin start");
		User nonExistingUser = authenticatorService.authenticate("sdsd", "sdsd");
		assertNull(nonExistingUser);
		logger.info("tryAuthenticatorServiceFailedLogin end");
	}
}