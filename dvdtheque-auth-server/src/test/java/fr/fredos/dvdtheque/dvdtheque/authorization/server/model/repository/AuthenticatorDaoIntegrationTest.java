package fr.fredos.dvdtheque.dvdtheque.authorization.server.model.repository;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.DvdthequeAuthServerApplication;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DvdthequeAuthServerApplication.class)
@ActiveProfiles("local")
public class AuthenticatorDaoIntegrationTest {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorDaoIntegrationTest.class);
	@Autowired
    private AuthenticatorDao authenticatorDao;
	
	@Test
	public void authenticate() {
		User user = authenticatorDao.authenticate("fredo", "fredo");
		assertNotNull(user);
	}
}
