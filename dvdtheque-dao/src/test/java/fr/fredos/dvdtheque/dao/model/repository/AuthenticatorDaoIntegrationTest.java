package fr.fredos.dvdtheque.dao.model.repository;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import fr.fredos.dvdtheque.dao.Application;
import fr.fredos.dvdtheque.dao.model.object.User;


@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class AuthenticatorDaoIntegrationTest {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorDaoIntegrationTest.class);
	@Autowired
    private AuthenticatorDao authenticatorDao;
	@Disabled
	@Test
	public void authenticate() {
		User user = authenticatorDao.authenticate("fredo", "fredo");
		assertNotNull(user);
		logger.info(user.toString());
	}
}
