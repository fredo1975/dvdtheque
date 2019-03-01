package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
	public static final String TITRE_FILM_UPDATED = "Lorem Ipsum updated";
	public static final Integer ANNEE = 2015;
	public static final String REAL_NOM = "toto titi";
	public static final String REAL_NOM1 = "Dan VanHarp";
	public static final String ACT1_NOM = "tata tutu";
	public static final String ACT2_NOM = "toitoi tuitui";
	public static final String ACT3_NOM = "tuotuo tmitmi";
	public static final String ACT4_NOM = "Graham Collins";
	
	@Autowired
	protected FilmDao filmDao;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	
	private void assertFilmIsNotNull(Film film) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()==3);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
	}
	@Test
	public void saveFilm() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
	}
	@Test
	public void findFilmByTitre() throws Exception{
		String methodName = "findFilmByTitre : ";
		logger.debug(methodName + "start");
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		film = filmService.findFilmByTitre(TITRE_FILM);
		assertFilmIsNotNull(film);
		logger.debug(methodName + "end");
	}
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.debug(methodName + "start");
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		logger.debug(methodName + "film ="+film.toString());
		for(Personne acteur : film.getActeurs()){
			logger.debug(methodName + " acteur="+acteur.toString());
		}
		logger.debug(methodName + "end");
	}
	@Test
	public void findFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		film = filmService.findFilm(film.getId());
		assertFilmIsNotNull(film);
		
	}
	@Test
	public void findAllFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = filmService.findAllFilms();
		assertNotNull(films);
		assertTrue(CollectionUtils.isNotEmpty(films));
		watch.stop();
	}
	@Test
	public void findAllTmdbFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		Set<Long> tmdbIds = new HashSet<>();
		tmdbIds.add(film.getTmdbId());
		Set<Long> films = filmService.findAllTmdbFilms(tmdbIds);
		assertNotNull(films);
		assertTrue(CollectionUtils.isNotEmpty(films));
		
	}
	@Test
	public void findAllRippedFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		List<Film> films = filmService.getAllRippedFilms();
		assertTrue(CollectionUtils.isNotEmpty(films));
		for(Film f : films){
			assertNotNull(f);
		}
	}
	
	@Test
	@Transactional
	public void updateFilm(){
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		film.setTitre(TITRE_FILM_UPDATED);
		Personne real = personneService.buildPersonne(REAL_NOM1);
		assertNotNull(real);
		Integer idreal = personneService.savePersonne(real);
		assertNotNull(idreal);
		real.setId(idreal);
		film.getRealisateurs().clear();
		film.getRealisateurs().add(real);
		
		Personne act = personneService.buildPersonne(ACT4_NOM);
		assertNotNull(act);
		Integer idAct = personneService.savePersonne(act);
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
		filmService.cleanAllFilms();
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaTitreService() {
		String methodName = "findAllFilmsByCriteriaTtireService : ";
		logger.debug(methodName + "start");
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(TITRE_FILM, 5),null,null,null,null);
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
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		Integer selectedActeurId = film.getActeurs().iterator().next().getId();
		logger.debug("selectedActeurId="+selectedActeurId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,selectedActeurId,null);
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
		
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto("Lorem",null,null,null,null);
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
	public void removeFilmDao() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		Personne real = film.getRealisateurs().iterator().next();
		assertNotNull(real);
		filmDao.removeFilm(film);
		Personne realisateur = personneService.findByPersonneId(real.getId());
		assertNotNull(realisateur);
	}
	/*
	@Test(expected = java.lang.Exception.class)
	public void removeFilmService() {
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		}else {
			film = filmService.findFilm(id);
		}
		assertNotNull(film);
		Personne real = film.getRealisateur();
		assertNotNull(real);
		
		filmService.removeFilm(film);
		Film deletedFilmDto = filmService.findFilm(id);
		assertNull(deletedFilmDto);
		Personne realDto = personneService.findByPersonneId(real.getId());
		assertNotNull(realDto);
	}*/
}
