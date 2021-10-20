package fr.fredos.dvdtheque.service;

import java.util.Random;

import org.easymock.EasyMockRunner;
import org.junit.runner.RunWith;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastTestSupport;

import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.dao.model.repository.PersonneDao;
import fr.fredos.dvdtheque.service.impl.FilmServiceImpl;
import fr.fredos.dvdtheque.service.impl.PersonneServiceImpl;
@RunWith(EasyMockRunner.class)
public abstract class BaseTest extends HazelcastTestSupport{
	protected FilmDao 				filmDao;
	protected PersonneServiceImpl 	personneService;
	protected PersonneDao 			personneDao;
	protected HazelcastInstance[] 	instances;
    protected FilmServiceImpl 		filmService;
	static final int 				INSTANCE_COUNT = 3;
    static final Random 			RANDOM = new Random();
    
    HazelcastInstance getInstance() {
        return instances[RANDOM.nextInt(INSTANCE_COUNT)];
    }
}
