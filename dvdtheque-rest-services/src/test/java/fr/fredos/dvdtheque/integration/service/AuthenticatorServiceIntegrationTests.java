package fr.fredos.dvdtheque.integration.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.dao.model.repository.AuthenticatorDao;
import fr.fredos.dvdtheque.service.IAuthenticatorService;
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class},
properties = { "eureka.client.enabled:false", "spring.cloud.config.enabled:false" })
public class AuthenticatorServiceIntegrationTests extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorServiceIntegrationTests.class);
	@Autowired
	protected AuthenticatorDao authenticatorDao;
	
	@Autowired
	protected IAuthenticatorService authenticatorService;
	
	@Test
	@Disabled
	public void tryAuthenticatorSuccessLogin() throws Exception{
		logger.info("tryAuthenticatorSuccessLogin start");
		User user = authenticatorService.saveUser("fredo", "fredo");
		assertNotNull(user);
		logger.info(user.toString());
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
