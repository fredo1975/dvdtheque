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
	}
	protected Config getConfig() {
        Config cfg = smallInstanceConfig();
        cfg.getMapConfig(FilmServiceImpl.CACHE_FILM).setTimeToLiveSeconds(1)
                .setStatisticsEnabled(false);
        return cfg;
    }
	@Test
	public void findAllFilmsWithEmptyCache() throws ParseException {
		List<Film> films = new ArrayList<>();
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films).times(2);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,40,FilmOrigine.TOUS);
		List<Film> _films = filmService.findAllFilms(filmDisplayTypeParam);
		EasyMock.verify(filmDao);
		assertNotNull(_films);
		assertEquals(0,_films.size());
	}
	@Test
	public void findAllFilmsWithNonEmptyCache() throws ParseException {
		final List<Film> films = new ArrayList<>();
		final Genre genre1 = new Genre(28,"Action");
		final Genre genre2 = new Genre(35,"Comedy");
		final Film film1 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
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
		film1.setId(25l);
		films.add(film1);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,40,FilmOrigine.TOUS);
		List<Film> _films = filmService.findAllFilms(filmDisplayTypeParam);
		EasyMock.verify(filmDao);
		assertNotNull(_films);
		assertEquals(1,_films.size());
		assertEquals(film1,_films.get(0));
	}
	
	@Test
	public void findFilmByTitreAndfindAllFilms() throws Exception {
		final List<Film> films = new ArrayList<>();
		final Genre genre1 = new Genre(28,"Action");
		final Genre genre2 = new Genre(35,"Comedy");
		final Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
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
		films.add(film);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.expect(filmDao.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844)).andReturn(film);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		
		final Film retrievedFilm = filmService.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		assertNotNull(retrievedFilm);
		List<Film> _films = filmService.findAllFilms(null);
		EasyMock.verify(filmDao);
		assertNotNull(_films);
		assertEquals(1,_films.size());
		assertEquals(film,_films.get(0));
	}
	
	@Test
	public void saveNewFilm() throws ParseException {
		final List<Film> films = new ArrayList<>();
		final long filmId = 25l;
		final Genre genre1 = new Genre(28,"Action");
		final Genre genre2 = new Genre(35,"Comedy");
		final Film filmNoId = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
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
		final Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
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
		film.setId(filmId);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.expect(filmDao.saveNewFilm(filmNoId)).andReturn(filmId);
		// we set filmId to filmNoId cause the saveNewFilm is mocked and thus don't set the id as it is when it is in the real world
		filmNoId.setId(filmId);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		final long filmIdRetrieved = filmService.saveNewFilm(filmNoId);
		final Film retrievedFilm = filmService.findFilm(filmId);
		EasyMock.verify(filmDao);
		assertNotNull(filmIdRetrieved);
		assertEquals(filmId,filmIdRetrieved);
		assertNotNull(retrievedFilm);
		FilmBuilder.assertFilmIsNotNull(retrievedFilm, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
	}
}
