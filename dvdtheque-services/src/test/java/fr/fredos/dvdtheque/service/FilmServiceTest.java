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
import com.hazelcast.test.TestHazelcastInstanceFactory;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmDisplayType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.service.impl.FilmServiceImpl;
import fr.fredos.dvdtheque.service.impl.PersonneServiceImpl;

public class FilmServiceTest extends BaseTest{
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
        cfg.getMapConfig(FilmServiceImpl.CACHE_GENRE).setTimeToLiveSeconds(1)
        .setStatisticsEnabled(false);
        cfg.getMapConfig(FilmServiceImpl.CACHE_ACTEUR).setTimeToLiveSeconds(1)
        .setStatisticsEnabled(false);
        return cfg;
    }
	@Test
	public void findAllActeursWithNonEmptyCache() throws ParseException {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		film.setId(FILM_ID_1);
		films.add(film);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);

		EasyMock.verify(filmDao);
		List<Personne> acteurs = filmService.findAllActeurs(new FilmDisplayTypeParam(FilmDisplayType.TOUS,40,FilmOrigine.TOUS));
		assertNotNull(acteurs);
		assertEquals(3,acteurs.size());
	}
	@Test
	public void findAllGenresWithNonEmptyCache() throws ParseException {
		final List<Film> films = new ArrayList<>();
		final Genre genre1 = new Genre(28,"Action");
		genre1.setId(28l);
		final Genre genre2 = new Genre(35,"Comedy");
		genre2.setId(35l);
		final List<Genre> genres = new ArrayList<>();
		genres.add(genre1);
		genres.add(genre2);
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		film1.setId(FILM_ID_1);
		films.add(film1);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.expect(filmDao.findAllGenres()).andReturn(genres);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		List<Genre> retrievedGenres = filmService.findAllGenres();
		EasyMock.verify(filmDao);
		assertNotNull(retrievedGenres);
		assertEquals(2, retrievedGenres.size());
		assertEquals(retrievedGenres, genres);
	}
	@Test
	public void findAllGenresWithEmptyCache() throws ParseException {
		final List<Film> films = new ArrayList<>();
		final List<Genre> genres = new ArrayList<>();
		final Genre genre1 = new Genre(28,"Action");
		genre1.setId(28l);
		final Genre genre2 = new Genre(35,"Comedy");
		genre2.setId(35l);
		//genres.add(genre1);
		//genres.add(genre2);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.expect(filmDao.findAllGenres()).andReturn(genres);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		
		List<Genre> retrievedGenres = filmService.findAllGenres();
		EasyMock.verify(filmDao);
		assertNotNull(retrievedGenres);
		assertEquals(0, retrievedGenres.size());
		assertEquals(retrievedGenres, genres);
	}
	@Test
	public void findFilmWithNonEmptyCache() throws ParseException {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		film1.setId(FILM_ID_1);
		films.add(film1);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		Film retrievedFilm = filmService.findFilm(FILM_ID_1);
		EasyMock.verify(filmDao);
		assertNotNull(retrievedFilm);
		assertEquals(retrievedFilm, film1);
	}
	@Test
	public void findFilmWithEmptyCache() throws ParseException {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		film1.setId(FILM_ID_1);
		//films.add(film1);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.expect(filmDao.findFilm(FILM_ID_1)).andReturn(film1);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		
		Film retrievedFilm = filmService.findFilm(FILM_ID_1);
		EasyMock.verify(filmDao);
		assertNotNull(retrievedFilm);
		assertEquals(retrievedFilm, film1);
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		film1.setId(FILM_ID_1);
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		film.setId(FILM_ID_1);
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
				.setZone(Integer.valueOf(2))
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		film.setId(FILM_ID_1);
		EasyMock.expect(filmDao.findAllFilms()).andReturn(films);
		EasyMock.expect(filmDao.saveNewFilm(filmNoId)).andReturn(FILM_ID_1);
		// we set filmId to filmNoId cause the saveNewFilm is mocked and thus don't set the id as it is when it is in the real world
		filmNoId.setId(FILM_ID_1);
		EasyMock.replay(filmDao);
		filmService = new FilmServiceImpl(filmDao,personneService,instances[0]);
		final long filmIdRetrieved = filmService.saveNewFilm(filmNoId);
		final Film retrievedFilm = filmService.findFilm(FILM_ID_1);
		EasyMock.verify(filmDao);
		assertNotNull(filmIdRetrieved);
		assertEquals(FILM_ID_1,filmIdRetrieved);
		assertNotNull(retrievedFilm);
		FilmBuilder.assertFilmIsNotNull(retrievedFilm, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
	}
}
