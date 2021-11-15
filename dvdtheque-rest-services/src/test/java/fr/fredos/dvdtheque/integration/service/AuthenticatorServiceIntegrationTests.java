package fr.fredos.dvdtheque.integration.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.config.AutoDetectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.dao.model.repository.AuthenticatorDao;
import fr.fredos.dvdtheque.service.IAuthenticatorService;
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class,
		AuthenticatorServiceIntegrationTests.HazelcastConfiguration.class},
properties = { "eureka.client.enabled:false", "spring.cloud.config.enabled:false" })
public class AuthenticatorServiceIntegrationTests extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorServiceIntegrationTests.class);
	@Autowired
	protected AuthenticatorDao authenticatorDao;
	
	@Autowired
	protected IAuthenticatorService authenticatorService;
	@TestConfiguration
	public static class HazelcastConfiguration {
		@Bean
		public HazelcastInstance hazelcastInstance() {
			Config config = new Config();
			config.getNetworkConfig().setJoin(new JoinConfig().setAutoDetectionConfig(new AutoDetectionConfig().setEnabled(false)));
			config.setInstanceName(RandomStringUtils.random(8, true, false))
					.addMapConfig(new MapConfig().setName("films"));
			return Hazelcast.newHazelcastInstance(config);
		}
	}
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
