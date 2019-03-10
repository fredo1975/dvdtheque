package fr.fredos.dvdtheque.dao.model.repository;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.Application;
import fr.fredos.dvdtheque.dao.model.object.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AuthenticatorDaoIntegrationTest {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorDaoIntegrationTest.class);
	@Autowired
    private AuthenticatorDao authenticatorDao;
	@Test
	@Ignore
	public void authenticate() {
		User user = authenticatorDao.authenticate("fredo", "fredo");
		assertNotNull(user);
		logger.info(user.toString());
	}
}
