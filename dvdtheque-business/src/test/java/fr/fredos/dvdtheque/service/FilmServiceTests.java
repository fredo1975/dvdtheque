package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})
public class FilmServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceTests.class);
	public static final String TITRE_FILM = "Lorem Ipsum";
	public static final String TITRE_FILM2 = "Lorem Ipsum2";
	public static final String TITRE_FILM_UPDATED = "Lorem Ipsum updated";
	public static final String TITRE_FILM_REUPDATED = "Lorem Ipsum reupdated";
	public static final Integer ANNEE = 2015;
	public static final String REAL_NOM = "toto titi";
	public static final String REAL_NOM1 = "Dan VanHarp";
	public static final String ACT1_NOM = "tata tutu";
	public static final String ACT2_NOM = "toitoi tuitui";
	public static final String ACT3_NOM = "tuotuo tmitmi";
	public static final String ACT4_NOM = "Graham Collins";
	public static final int RIP_DATE_OFFSET = -10;
	public static final int RIP_DATE_OFFSET2 = -1;
	@Autowired
	protected FilmDao filmDao;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	
	private void assertFilmIsNotNull(Film film, int ripDateOffset) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		assertNotNull(film.getDvd().getDateRip());
		assertEquals(filmService.clearDate(createRipDate(ripDateOffset)),film.getDvd().getDateRip());
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()==3||film.getActeurs().size()==2);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
		
	}
	private Date createRipDate(int ripDateOffset) {
		Calendar cal = Calendar.getInstance();
		return DateUtils.addDays(cal.getTime(), ripDateOffset);
	}
	@Test
	public void saveFilm() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
	}
	@Test
	public void findFilmByTitre() throws Exception{
		String methodName = "findFilmByTitre : ";
		logger.debug(methodName + "start");
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		Film retrievedFilm = filmService.findFilmByTitre(TITRE_FILM);
		assertFilmIsNotNull(retrievedFilm, RIP_DATE_OFFSET);
		logger.debug(methodName + "end");
	}
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.debug(methodName + "start");
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		Film retrievedFilm = filmService.findFilmWithAllObjectGraph(film.getId());
		assertFilmIsNotNull(retrievedFilm, RIP_DATE_OFFSET);
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
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		film = filmService.findFilm(film.getId());
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		assertNotNull(film.getDvd());
	}
	@Test
	public void findAllFilms() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		Film film2 = filmService.createOrRetrieveFilm(TITRE_FILM_UPDATED, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film2, RIP_DATE_OFFSET);
		Film film3 = filmService.createOrRetrieveFilm(TITRE_FILM_REUPDATED, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film3, RIP_DATE_OFFSET);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = filmService.findAllFilms();
		assertNotNull(films);
		assertTrue(CollectionUtils.isNotEmpty(films));
		assertTrue(films.size()==3);
		for(Film f : films) {
			logger.info(film.toString());
		}
		watch.stop();
		logger.info(watch.prettyPrint());
		filmService.cleanAllFilms();
	}
	@Test
	public void findAllTmdbFilms() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		Set<Long> tmdbIds = new HashSet<>();
		tmdbIds.add(film.getTmdbId());
		Set<Long> films = filmService.findAllTmdbFilms(tmdbIds);
		assertNotNull(films);
		assertTrue(CollectionUtils.isNotEmpty(films));
		
	}
	@Test
	public void findAllRippedFilms() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		List<Film> films = filmService.getAllRippedFilms();
		assertTrue(CollectionUtils.isNotEmpty(films));
		for(Film f : films){
			assertNotNull(f);
		}
	}
	
	@Test
	@Transactional
	public void updateFilm(){
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		
		film.setTitre(TITRE_FILM_UPDATED);
		Personne real = personneService.buildPersonne(REAL_NOM1);
		assertNotNull(real);
		Long idreal = personneService.savePersonne(real);
		assertNotNull(idreal);
		real.setId(idreal);
		film.getRealisateurs().clear();
		film.getRealisateurs().add(real);
		
		Personne act = personneService.buildPersonne(ACT4_NOM);
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
		assertEquals(StringUtils.upperCase(TITRE_FILM_UPDATED), filmUpdated.getTitre());
		assertEquals(REAL_NOM1, filmUpdated.getRealisateurs().iterator().next().getNom());
		assertEquals(ACT4_NOM, filmUpdated.getActeurs().iterator().next().getNom());
		assertEquals(posterPath, filmUpdated.getPosterPath());
	}
	@Test
	public void cleanAllFilms() {
		String methodName = "cleanAllFilms : ";
		logger.debug(methodName + "start");
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		Film film2 = filmService.createOrRetrieveFilm(TITRE_FILM_UPDATED, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film2, RIP_DATE_OFFSET);
		Film film3 = filmService.createOrRetrieveFilm(TITRE_FILM_REUPDATED, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film3, RIP_DATE_OFFSET);
		filmService.cleanAllFilms();
		assertTrue(CollectionUtils.isEmpty(filmService.findAllFilms()));
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaTitreService() {
		String methodName = "findAllFilmsByCriteriaTtireService : ";
		logger.debug(methodName + "start");
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(TITRE_FILM, 5),null,null,null,null, null);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f : films){
			logger.debug(f.toString());
		}
		assertEquals(1, films.size());
		assertEquals(StringUtils.upperCase(TITRE_FILM),films.get(0).getTitre());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaActeursService() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		
		Long selectedActeurId = film.getActeurs().iterator().next().getId();
		logger.debug("selectedActeurId="+selectedActeurId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,selectedActeurId,null, null);
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
		String methodName = "findAllFilmsByCriteria : ";
		logger.debug(methodName + "start");
		
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto("Lorem",null,null,null,null, null);
		List<Film> films = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(1, films.size());
		Film f2 = films.get(0);
		assertEquals(StringUtils.upperCase(TITRE_FILM),f2.getTitre());
		Set<Personne> realisateurSet = f2.getRealisateurs();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		Personne real = personneService.findRealisateurByFilm(film);
		assertEquals(real.getNom(),realisateur.getNom());
		assertEquals(real.getPrenom(),realisateur.getPrenom());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaRippedSinceDao() {
		String methodName = "findAllFilmsByCriteriaRippedSinceDao : ";
		logger.debug(methodName + "start");
		
		Film film1 = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film1, RIP_DATE_OFFSET);
		Film film2 = filmService.createOrRetrieveFilm(TITRE_FILM2, ANNEE,REAL_NOM1,ACT4_NOM,null,null, createRipDate(RIP_DATE_OFFSET2));
		assertFilmIsNotNull(film2, RIP_DATE_OFFSET2);
		
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,null,null, Boolean.TRUE);
		List<Film> films = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(2, films.size());
		Film f2 = films.get(0);
		assertEquals(StringUtils.upperCase(TITRE_FILM2),f2.getTitre());
		Set<Personne> realisateurSet = f2.getRealisateurs();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		Personne real = personneService.findRealisateurByFilm(film2);
		assertEquals(real.getNom(),realisateur.getNom());
		assertEquals(real.getPrenom(),realisateur.getPrenom());
		logger.debug(methodName + "end");
	}
	@Test
	public void removeFilmDao() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		Personne real = film.getRealisateurs().iterator().next();
		assertNotNull(real);
		filmDao.removeFilm(film);
		Film removedFilm = filmService.findFilmByTitre(film.getTitre());
		assertNull(removedFilm);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void removeFilmService() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		filmService.removeFilm(film);
		Film deletedFilm = filmService.findFilm(film.getId());
		assertNull(deletedFilm);
	}
	
	@Test
	public void checkIfTmdbFilmExists() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, createRipDate(RIP_DATE_OFFSET));
		assertFilmIsNotNull(film, RIP_DATE_OFFSET);
		Boolean exists = filmService.checkIfTmdbFilmExists(film.getTmdbId());
		assertTrue(exists);
	}
}
