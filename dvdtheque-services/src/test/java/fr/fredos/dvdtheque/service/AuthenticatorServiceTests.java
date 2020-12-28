package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.dao.model.repository.AuthenticatorDao;
@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})
public class AuthenticatorServiceTests extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorServiceTests.class);
	@Autowired
	protected AuthenticatorDao authenticatorDao;
	
	@Autowired
	protected IAuthenticatorService authenticatorService;
	
	@Test
	public void tryAuthenticatorSuccessLogin() throws Exception{
		logger.info("tryAuthenticatorSuccessLogin start");
		/*User user = authenticatorService.saveUser("fredo", "fredo");
		assertNotNull(user);*/
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
