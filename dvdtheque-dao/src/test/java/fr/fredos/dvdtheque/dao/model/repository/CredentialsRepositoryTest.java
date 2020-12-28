package fr.fredos.dvdtheque.dao.model.repository;

import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.Application;
import fr.fredos.dvdtheque.dao.model.object.CredentialsRepository;
import fr.fredos.dvdtheque.dao.model.object.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CredentialsRepositoryTest {
	protected Logger logger = LoggerFactory.getLogger(CredentialsRepositoryTest.class);
	@Autowired
	private CredentialsRepository credentialsRepository;
	
	@Test
	public void contextLoads() {
	}
	@Test
	public void findByName() {
		Optional<User> possibleCredentials = credentialsRepository.findUserWithName("fredo");
		User user = possibleCredentials.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		assertNotNull(user);
	}
}
