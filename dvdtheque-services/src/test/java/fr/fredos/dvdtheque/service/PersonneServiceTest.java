package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.PersonneDao;
import fr.fredos.dvdtheque.service.impl.PersonneServiceImpl;

@RunWith(EasyMockRunner.class)
public class PersonneServiceTest extends HazelcastTestSupport{
	static final int 			INSTANCE_COUNT = 3;
    static final Random 		RANDOM = new Random();
    private HazelcastInstance[] instances;
    private PersonneServiceImpl personneService;
    private PersonneDao 		personneDao;
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
