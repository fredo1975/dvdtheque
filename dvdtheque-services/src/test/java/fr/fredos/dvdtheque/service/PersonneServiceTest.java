package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.TestHazelcastInstanceFactory;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.PersonneDao;
import fr.fredos.dvdtheque.service.impl.PersonneServiceImpl;

public class PersonneServiceTest extends BaseTest{
    HazelcastInstance getInstance() {
        return instances[RANDOM.nextInt(INSTANCE_COUNT)];
    }
	@BeforeEach
	public void init() throws ParseException {
		TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(INSTANCE_COUNT);
        Config config = getConfig();
        instances = factory.newInstances(config);
        personneDao = EasyMock.mock(PersonneDao.class);
	}
	protected Config getConfig() {
        Config cfg = smallInstanceConfig();
        cfg.getMapConfig(PersonneServiceImpl.CACHE_PERSONNE).setTimeToLiveSeconds(1)
                .setStatisticsEnabled(false);
        return cfg;
    }
	
	@Test
	public void findAllPersonnesWithEmptyCache() throws Exception {
		List<Personne> personnesList = new ArrayList<>();
		EasyMock.expect(personneDao.findAllPersonne()).andReturn(personnesList);
		EasyMock.replay(personneDao);
		personneService = new PersonneServiceImpl(personneDao, instances[0]);
		List<Personne> _personnesList = personneService.findAllPersonne();
		EasyMock.verify(personneDao);
		assertNotNull(_personnesList);
		assertEquals(0,_personnesList.size());
	}
}
