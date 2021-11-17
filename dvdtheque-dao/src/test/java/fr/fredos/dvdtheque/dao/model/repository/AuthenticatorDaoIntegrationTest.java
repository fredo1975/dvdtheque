package fr.fredos.dvdtheque.dao.model.repository;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.dao.model.repository.impl.AuthenticatorDaoImpl;


@ExtendWith(SpringExtension.class)
@Import(AuthenticatorDaoImpl.class)
@DataJpaTest
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
