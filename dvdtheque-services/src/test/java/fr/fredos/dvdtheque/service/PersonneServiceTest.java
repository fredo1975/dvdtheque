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
		/*
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null);
		List<Personne> personneList = personneService.findAllPersonne();
		assertNotNull(personneList);
		assertTrue(CollectionUtils.isNotEmpty(personneList));
		assertTrue("personneList.size() should be 4 but is "+personneList.size(),personneList.size()==4);
		for (Personne personne : personneList) {
			Personne p = personneService.findByPersonneId(personne.getId());
			assertNotNull(p);
		}
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null);
		List<Personne> personne2List = personneService.findAllPersonne();
		assertNotNull(personne2List);
		assertTrue(CollectionUtils.isNotEmpty(personne2List));
		assertTrue(personne2List.size()==4);
		for (Personne personne : personne2List) {
			Personne p = personneService.findByPersonneId(personne.getId());
			assertNotNull(p);
		}*/
		personneService = new PersonneServiceImpl(personneDao, instances[0]);
		List<Personne> _personnesList = personneService.findAllPersonne();
		EasyMock.verify(personneDao);
		assertNotNull(_personnesList);
		assertEquals(0,_personnesList.size());
	}
}
