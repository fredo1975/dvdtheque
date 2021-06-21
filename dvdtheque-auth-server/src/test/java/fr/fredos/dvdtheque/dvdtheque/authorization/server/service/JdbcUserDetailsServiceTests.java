package fr.fredos.dvdtheque.dvdtheque.authorization.server.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.DvdthequeAuthServerApplication;

@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DvdthequeAuthServerApplication.class})
public class JdbcUserDetailsServiceTests extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private JdbcUserDetailsService jdbcUserDetailsService;
	
	@Test
    public void loadUserByUsername(){
		UserDetails userDetails = jdbcUserDetailsService.loadUserByUsername("fredo");
		assertNotNull(userDetails);
		logger.info(userDetails.toString());
    }
}
