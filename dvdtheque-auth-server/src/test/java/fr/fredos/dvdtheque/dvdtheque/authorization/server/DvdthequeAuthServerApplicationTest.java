package fr.fredos.dvdtheque.dvdtheque.authorization.server;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)

@SpringBootTest(classes = DvdthequeAuthServerApplication.class)
public class DvdthequeAuthServerApplicationTest {
	@Autowired
	private UserDetailsService jdbcUserDetailsService;
	@Test
	public void contextLoads() {
	}
	@Test
	public void loadUserByUsername() {
		UserDetails userDetails = jdbcUserDetailsService.loadUserByUsername("fredo");
		assertNotNull(userDetails);
	}
}
