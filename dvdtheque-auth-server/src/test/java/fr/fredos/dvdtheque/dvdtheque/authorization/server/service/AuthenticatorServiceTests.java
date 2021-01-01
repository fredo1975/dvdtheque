package fr.fredos.dvdtheque.dvdtheque.authorization.server.service;

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

import fr.fredos.dvdtheque.dvdtheque.authorization.server.DvdthequeAuthServerApplication;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.User;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.repository.AuthenticatorDao;
@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DvdthequeAuthServerApplication.class})
public class AuthenticatorServiceTests extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorServiceTests.class);
	@Autowired
	protected AuthenticatorDao authenticatorDao;
	@Autowired
	protected IAuthenticatorService authenticatorService;
	
	@Test
	public void tryAuthenticatorSuccessLogin() throws Exception{
		/*User user = authenticatorService.saveUser("fredo", "fredo");
		assertNotNull(user);*/
		User existingUser = authenticatorDao.authenticate("fredo", "fredo");
		assertNotNull(existingUser);
		logger.info(existingUser.toString());
	}
	@Test
	public void tryAuthenticatorDaoFailedLogin(){
		User nonExistingUser = authenticatorDao.authenticate("sdsd", "sdsd");
		assertNull(nonExistingUser);
	}
	@Test
	public void tryAuthenticatorServiceFailedLogin(){
		User nonExistingUser = authenticatorService.authenticate("sdsd", "sdsd");
		assertNull(nonExistingUser);
	}
	@Test
	public void tryAuthenticatorServiceSuccessLogin() throws Exception{
		User existingUser = authenticatorService.authenticate("fredo", "fredo");
		assertNotNull(existingUser);
		logger.info(existingUser.toString());
	}
}