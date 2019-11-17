package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})
@ActiveProfiles("local")
public class FilmServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceTests.class);
	
	@Autowired
	private FilmDao filmDao;
	@Autowired
	private IFilmService filmService;
	@Autowired
	private IPersonneService personneService;
	@Autowired
	private ExcelFilmHandler excelFilmHandler;
	@Autowired
    private CacheManager cacheManager;
	
	@Before
	public void cleanAllCaches() {
		filmService.cleanAllCaches();
	}
	
	@Test
	public void saveNewFilm() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, true);
	}
	
	@Test
	public void findFilmByTitre() throws Exception{
		String methodName = "findFilmByTitre : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film retrievedFilm = filmService.findFilmByTitre(film.getTitre());
		FilmBuilder.assertFilmIsNotNull(retrievedFilm, false,FilmBuilder.RIP_DATE_OFFSET, true);
		logger.debug(methodName + "end");
	}
	
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film retrievedFilm = filmService.findFilmWithAllObjectGraph(film.getId());
		FilmBuilder.assertFilmIsNotNull(retrievedFilm,false, FilmBuilder.RIP_DATE_OFFSET, true);
		logger.debug(methodName + "retrievedFilm ="+retrievedFilm.toString());
		for(Personne acteur : retrievedFilm.getActeurs()){
			logger.debug(methodName + " acteur="+acteur.toString());
		}
		for(Personne realisateur : retrievedFilm.getRealisateurs()){
			logger.debug(methodName + " realisateur="+realisateur.toString());
		}
		logger.debug(methodName + "end");
	}
	
	@Test
	public void findFilm() throws Exception {
		logger.info("Using cache manager: " + cacheManager.getClass().getName());
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		film = filmService.findFilm(film.getId());
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, true);
		assertNotNull(film.getDvd());
	}
	
	@Test
	public void findAllGenres() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Genre> genres = filmService.findAllGenres();
		assertTrue(CollectionUtils.isNotEmpty(genres));
	}
	
	@Test
	public void findAllDvd() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		assertNotNull(filmId3);
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId4 = filmService.saveNewFilm(film4);
		assertNotNull(filmId4);
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId5 = filmService.saveNewFilm(film5);
		assertNotNull(filmId5);
		
		List<Film> films = filmService.findAllFilmsByOrigine(FilmOrigine.DVD);
		assertNotNull(films);
		assertTrue(CollectionUtils.isNotEmpty(films));
		assertTrue(films.size()==2);
		
	}
	
	@Test
	public void findAllTmdbFilms() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Set<Long> tmdbIds = new HashSet<>();
		tmdbIds.add(film.getTmdbId());
		Set<Long> films = filmService.findAllTmdbFilms(tmdbIds);
		assertNotNull(films);
		assertTrue(CollectionUtils.isNotEmpty(films));
		
	}
	
	@Test
	public void findAllRippedFilms() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Film> films = filmService.getAllRippedFilms();
		assertTrue(CollectionUtils.isNotEmpty(films));
		for(Film f : films){
			assertNotNull(f);
		}
	}
	
	@Test
	public void updateFilm(){
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		film.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		Personne real = personneService.buildPersonne(FilmBuilder.REAL_NOM_TMBD_ID_4780, null);
		assertNotNull(real);
		Long idreal = personneService.savePersonne(real);
		assertNotNull(idreal);
		real.setId(idreal);
		film.getRealisateurs().clear();
		film.getRealisateurs().add(real);
		
		Personne act = personneService.buildPersonne(FilmBuilder.ACT4_TMBD_ID_844, null);
		assertNotNull(act);
		Long idAct = personneService.savePersonne(act);
		assertNotNull(idAct);
		act.setId(idAct);
		film.getActeurs().clear();
		film.getActeurs().add(act);
		
		final String posterPath = "posterPath";
		film.setPosterPath(posterPath);
		filmService.updateFilm(film);
		Film filmUpdated = filmService.findFilm(film.getId());
		
		assertNotNull(filmUpdated);
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), filmUpdated.getTitre());
		assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_4780, filmUpdated.getRealisateurs().iterator().next().getNom());
		assertEquals(FilmBuilder.ACT4_TMBD_ID_844, filmUpdated.getActeurs().iterator().next().getNom());
		assertEquals(posterPath, filmUpdated.getPosterPath());
	}
	@Test
	public void cleanAllFilms() {
		String methodName = "cleanAllFilms : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		assertNotNull(filmId3);
		filmService.cleanAllFilms();
		assertTrue(CollectionUtils.isEmpty(filmService.findAllFilms()));
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaTitreService() {
		String methodName = "findAllFilmsByCriteriaTtireService : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(FilmBuilder.TITRE_FILM_TMBD_ID_844, 5),null,null,null,null, null, null);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f : films){
			logger.debug(f.toString());
		}
		assertEquals(1, films.size());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844),films.get(0).getTitre());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaFilmOrigineService() {
		String methodName = "findAllFilmsByCriteriaFilmOrigineService : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film dvdFilm = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long dvdFilmId = filmService.saveNewFilm(dvdFilm);
		assertNotNull(dvdFilmId);
		Film enSalleFilm = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long enSalleFilmId = filmService.saveNewFilm(enSalleFilm);
		assertNotNull(enSalleFilmId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,null,null, null, FilmOrigine.EN_SALLE);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f : films){
			logger.debug(f.toString());
		}
		assertEquals(1, films.size());
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780),films.get(0).getTitre());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaActeursService() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Long selectedActeurId = film.getActeurs().iterator().next().getId();
		logger.debug("selectedActeurId="+selectedActeurId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,selectedActeurId,null, null, null);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(1, films.size());
		Film f2 = films.get(0);
		assertNotNull(f2);
		assertNotNull(f2.getActeurs());
		Optional<Personne> op = f2.getActeurs().stream().filter(acteurDto -> acteurDto.getId().equals(selectedActeurId)).findAny();
		Personne acteur = op.get();
		assertNotNull(acteur);
		assertEquals(selectedActeurId, acteur.getId());
	}
	@Test
	public void findAllFilmsByCriteriaDao() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(FilmBuilder.TITRE_FILM_TMBD_ID_844, 2),null,null,null,null, null, null);
		List<Film> films = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(1, films.size());
		Film f2 = films.get(0);
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844),f2.getTitre());
		Set<Personne> realisateurSet = f2.getRealisateurs();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		Personne real = personneService.findRealisateurByFilm(film);
		assertEquals(real.getNom(),realisateur.getNom());
		assertEquals(real.getPrenom(),realisateur.getPrenom());
	}
	@Test
	@Ignore
	public void findAllFilmsByCriteriaRippedSinceDao() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2)).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,null,null, Boolean.TRUE, null);
		List<Film> films = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(2, films.size());
		Film f2 = films.get(0);
		assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780),f2.getTitre());
		Set<Personne> realisateurSet = f2.getRealisateurs();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		Personne real = personneService.findRealisateurByFilm(film2);
		assertEquals(real.getNom(),realisateur.getNom());
		assertEquals(real.getPrenom(),realisateur.getPrenom());
	}
	@Test
	public void removeFilmDao() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Personne real = film.getRealisateurs().iterator().next();
		assertNotNull(real);
		filmDao.removeFilm(film);
		Film removedFilm = filmService.findFilmByTitre(film.getTitre());
		assertNull(removedFilm);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void removeFilmService() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		filmService.removeFilm(film);
		Film deletedFilm = filmService.findFilm(film.getId());
		assertNull(deletedFilm);
	}
	
	@Test
	public void checkIfTmdbFilmExists() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Boolean exists = filmService.checkIfTmdbFilmExists(film.getTmdbId());
		assertTrue(exists);
	}
	
	@Test
	public void testExcelToCsv() throws IOException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		assertNotNull(filmId3);
		List<Film> list = filmService.findAllFilms();
		assertNotNull(list);
	    byte[] excelContent = this.excelFilmHandler.createByteContentFromFilmList(list);
	    assertNotNull(excelContent);
	    Workbook workBook = this.excelFilmHandler.createSheetFromByteArray(excelContent);
	    assertNotNull(workBook);
		workBook.forEach(sheet -> {
        	assertEquals(FilmBuilder.SHEET_NAME, sheet.getSheetName());
        });
		Sheet sheet = workBook.getSheetAt(0);
        assertEquals(FilmBuilder.SHEET_NAME, sheet.getSheetName());
        String csv = this.excelFilmHandler.createCsvFromExcel(workBook);
        assertNotNull(csv);
	}
	@Test
	public void testCreateSXSSFWorkbookFromFilmList() throws IOException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setVu(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setRipped(true)
				.setVu(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_1271)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_1271)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_1271)
				.setRipped(true)
				.setVu(false)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_1271)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		assertNotNull(filmId3);
		List<Film> list = filmService.findAllFilms();
		assertNotNull(list);
		
		byte[] excelContent = this.excelFilmHandler.createByteContentFromFilmList(list);
		assertNotNull(excelContent);
		Workbook workBook = excelFilmHandler.createSheetFromByteArray(excelContent);
		assertNotNull(workBook);
		workBook.forEach(sheet -> {
        	assertEquals(FilmBuilder.SHEET_NAME, sheet.getSheetName());
        });
		Sheet sheet = workBook.getSheetAt(0);
        assertEquals(FilmBuilder.SHEET_NAME, sheet.getSheetName());
        DataFormatter dataFormatter = new DataFormatter();
        sheet.forEach(row -> {
        	if(row.getRowNum()==1) {
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_844, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), StringUtils.upperCase(cellValue));
                    }
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE, new Integer(cellValue));
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertTrue(cellValue.contains(FilmBuilder.ACT1_TMBD_ID_844));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT2_TMBD_ID_844));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT3_TMBD_ID_844));
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==7) {
                    	assertEquals(FilmBuilder.ZONE_DVD, cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals(DvdFormat.DVD.name(), cellValue);
                    }
                });
        	}else if(row.getRowNum()==2) {
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_1271, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_1271), StringUtils.upperCase(cellValue));
                    }
                    
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE, new Integer(cellValue));
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertTrue(cellValue.contains(FilmBuilder.ACT1_TMBD_ID_1271));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT2_TMBD_ID_1271));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT3_TMBD_ID_1271));
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.EN_SALLE.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("non", cellValue);
                    }
                    if(cell.getColumnIndex()==7) {
                    	assertEquals(FilmBuilder.ZONE_DVD, cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals(DvdFormat.DVD.name(), cellValue);
                    }
                });
        	}else if(row.getRowNum()==3) {
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_4780, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), StringUtils.upperCase(cellValue));
                    }
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE, new Integer(cellValue));
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertTrue(cellValue.contains(FilmBuilder.ACT3_TMBD_ID_4780));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT1_TMBD_ID_4780));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT2_TMBD_ID_4780));
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==7) {
                    	assertEquals(FilmBuilder.ZONE_DVD, cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals(DvdFormat.DVD.name(), cellValue);
                    }
                });
        	}
        });
	}
	
	@Test
	public void findAllRealisateurs() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Personne> realList = filmService.findAllRealisateurs();
		assertNotNull(realList);
		assertTrue(CollectionUtils.isNotEmpty(realList));
		assertTrue(realList.size()==1);
	}
	@Test
	public void findAllRealisateursByOrigine() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Personne> realList = filmService.findAllRealisateursByOrigine(FilmOrigine.DVD);
		assertNotNull(realList);
		assertTrue(CollectionUtils.isNotEmpty(realList));
		assertTrue(realList.size()==1);
	}
	@Test
	public void findAllActeurs() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		List<Personne> actList = filmService.findAllActeurs();
		assertNotNull(actList);
		assertTrue(CollectionUtils.isNotEmpty(actList));
		assertTrue(actList.size()==3);
	}
	@Test
	public void findAllActeursByOrigine() {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film filmDvd = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmDvdId = filmService.saveNewFilm(filmDvd);
		assertNotNull(filmDvdId);
		Film filmEnSalle = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmEnSalleId = filmService.saveNewFilm(filmEnSalle);
		assertNotNull(filmEnSalleId);
		List<Personne> actEnSalleIdList = filmService.findAllActeursByOrigine(FilmOrigine.EN_SALLE);
		assertNotNull(actEnSalleIdList);
		assertTrue(CollectionUtils.isNotEmpty(actEnSalleIdList));
		assertTrue(actEnSalleIdList.size()==2);
		List<Personne> actDvdIdList = filmService.findAllActeursByOrigine(FilmOrigine.DVD);
		assertNotNull(actDvdIdList);
		assertTrue(CollectionUtils.isNotEmpty(actDvdIdList));
		assertTrue(actDvdIdList.size()==3);
		
		Film filmEnSalle2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_1271)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_1271)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_1271)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_1271)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmEnSalleId2 = filmService.saveNewFilm(filmEnSalle2);
		assertNotNull(filmEnSalleId2);
		actEnSalleIdList = filmService.findAllActeursByOrigine(FilmOrigine.EN_SALLE);
		assertNotNull(actEnSalleIdList);
		assertTrue(CollectionUtils.isNotEmpty(actEnSalleIdList));
		assertTrue(actEnSalleIdList.size()==5);
	}
}
