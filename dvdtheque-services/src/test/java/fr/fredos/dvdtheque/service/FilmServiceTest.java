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

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmDisplayType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.service.impl.FilmServiceImpl;
import fr.fredos.dvdtheque.service.impl.PersonneServiceImpl;
	
@RunWith(EasyMockRunner.class)
public class FilmServiceTest extends HazelcastTestSupport{
	private FilmDao 			filmDao;
	private PersonneServiceImpl personneService;
	static final int 			INSTANCE_COUNT = 3;
    static final Random 		RANDOM = new Random();
    private HazelcastInstance[] instances;
	private FilmServiceImpl 	filmService;
    HazelcastInstance getInstance() {
        return instances[RANDOM.nextInt(INSTANCE_COUNT)];
    }
	@BeforeEach
	public void init() throws ParseException {
		TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(INSTANCE_COUNT);
        Config config = getConfig();
        instances = factory.newInstances(config);
        filmDao = EasyMock.mock(FilmDao.class);
		personneService = EasyMock.mock(PersonneServiceImpl.class);
		Genre genre1 = new Genre(28,"Action");
		Genre genre2 = new Genre(35,"Comedy");
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
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
		film.setId(25l);
		List<Film> films = new ArrayList<>();
		films.add(film);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films).anyTimes();
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		//EasyMock.verify(filmDao);
	}
	protected Config getConfig() {
        Config cfg = smallInstanceConfig();
        cfg.getMapConfig(FilmServiceImpl.CACHE_FILM).setTimeToLiveSeconds(1)
                .setStatisticsEnabled(false);
        return cfg;
    }
	@Test
	public void findAllFilms() throws ParseException {
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,40,FilmOrigine.TOUS);
		List<Film> _films = filmService.findAllFilms(filmDisplayTypeParam);
		EasyMock.verify(filmDao);
		assertNotNull(_films);
		assertEquals(1,_films.size());
	}
}
