package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import fr.fredos.dvdtheque.common.dto.NewActeurDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.service.dto.FilmUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})
public class FilmServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceTests.class);
	@Autowired
	protected FilmDao filmDao;
	@Autowired
	protected FilmService filmService;
	@Autowired
	protected PersonneService personneService;
	private final static String MAX_ID_SQL = "select max(id) from FILM";
	
	@Test
	public void findFilmByTitre() throws Exception{
		String methodName = "findFilmByTitre : ";
		logger.debug(methodName + "start");
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		filmService.saveNewFilm(film);
		film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		
		//FilmDto filmNotFound = filmService.findFilmByTitre("blade runne");
		logger.debug(methodName + "end");
	}
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
			Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
			filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null));
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		}else {
			film = filmService.findFilm(id);
		}
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug(methodName + "film ="+film.toString());
		assertNotNull(film.getActeurs());
		assertNotNull(film.getRealisateurs());
		
		for(Personne acteur : film.getActeurs()){
			logger.debug(methodName + " acteur="+acteur.toString());
		}
		logger.debug(methodName + "end");
	}
	@Test
	public void findFilm() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
			Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
			film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
			id = filmService.saveNewFilm(film);
			assertNotNull(id);
		}else {
			film = filmService.findFilm(id);
		}
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug("film ="+film.toString());
		assertNotNull(film.getRealisateurs());
		
		logger.debug("Realisateur ="+film.getRealisateurs().iterator().next());
		assertNotNull(film.getActeurs());
		for(Personne acteur : film.getActeurs()){
			logger.debug("Acteur="+acteur.toString());
		}
		
	}
	@Test
	public void findAllFilm() throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = filmService.findAllFilms();
		assertNotNull(films);
		watch.stop();
	}
	@Test
	public void findAllRippedFilm() throws Exception {
		List<Film> films = filmService.getAllRippedFilms();
		for(Film film : films){
			assertNotNull(film);
			assertNotNull(film.getTitre());
		}
	}
	
	@Test
	@Transactional
	public void saveNewFilmWithNewPersons() {
		String methodName = "saveNewFilmWithNewPersons : ";
		logger.debug(methodName + "start");
		Set<NewActeurDto> newActeurDtoSet = new HashSet<>();
		NewActeurDto newActeurDto = FilmUtils.buildNewActeurDto();
		newActeurDtoSet.add(newActeurDto);
		
		Personne real = FilmUtils.buildPersonne(FilmUtils.REAL_NOM,FilmUtils.REAL_PRENOM);
		personneService.savePersonne(real);
		real = personneService.findPersonneByFullName(FilmUtils.REAL_NOM,FilmUtils.REAL_PRENOM);
		assertNotNull(real);
		
		Dvd dvd = new Dvd();
		dvd.setZone(new Integer(2));
		dvd.setAnnee(2002);
		dvd.setEdition("edition");
		Film film = new Film();
		film.setTitre("test1");
		film.setAnnee(2002);
		film.setDvd(dvd);
		
		film.getRealisateurs().add(real);
		film.setNewActeurDtoSet(newActeurDtoSet);
		Integer id = filmService.saveNewFilm(film);
		assertNotNull(id);
		logger.debug(methodName + "end");
	}
	@Test
	@Transactional
	public void saveNewFilmWithExistingPersons() {
		String methodName = "saveNewFilmWithExistingPersons : ";
		logger.debug(methodName + "start");
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		
		Integer id = filmService.saveNewFilm(film);
		assertNotNull(id);
		assertNotNull(film.getRealisateurs());
		Iterator<Personne> iterator = film.getRealisateurs().iterator();
		assertNotNull(iterator);
		Personne realisateur = iterator.next();
		assertNotNull(realisateur.getId());
		Integer idReal = realisateur.getId();
		assertNotNull(idReal);
		realisateur = personneService.findByPersonneId(idReal);
		assertNotNull(realisateur);
		assertNotNull(film.getActeurs().iterator());
		Integer idAct = film.getActeurs().iterator().next().getId();
		assertNotNull(idAct);
		Personne acteur = personneService.findByPersonneId(idAct);
		assertNotNull(acteur);
		Dvd dvd = new Dvd();
		dvd.setZone(new Integer(2));
		dvd.setAnnee(2002);
		dvd.setEdition("edition");
		Film newFilm = new Film();
		newFilm.setTitre(FilmUtils.TITRE_FILM_UPDATED);
		newFilm.setAnnee(2002);
		newFilm.setDvd(dvd);
		newFilm.getRealisateurs().add(realisateur);
		newFilm.getActeurs().add(acteur);
		
		id = filmService.saveNewFilm(newFilm);
		assertNotNull(id);
		Film filmSaved = filmService.findFilm(id);
		assertNotNull(filmSaved);
		logger.debug(methodName + "end");
	}
	@Test
	@Transactional
	public void updateFilmWithNewPersons(){
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
			Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
			film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
			id = filmService.saveNewFilm(film);
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		}else {
			film = filmService.findFilm(id);
			assertNotNull(film);
		}
		assertNotNull(film);
		Set<NewActeurDto> newActeurDtoSet = new HashSet<>();
		NewActeurDto newActeurDto = FilmUtils.buildNewActeurDto();
		newActeurDtoSet.add(newActeurDto);
		film.setNewActeurDtoSet(newActeurDtoSet);
		filmService.updateFilm(film);
		film = filmService.findFilm(id);
		assertNotNull(film);
	}
	@Test
	@Transactional
	public void updateFilm(){
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
			Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
			film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
			id = filmService.saveNewFilm(film);
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		}else {
			film = filmService.findFilm(id);
			assertNotNull(film);
		}
		assertNotNull(film);
		film.setTitre(FilmUtils.TITRE_FILM_UPDATED);
		List<Personne> realList = personneService.findAllRealisateur();
		assertNotNull(realList);
		assertTrue(realList.size()>0);
		Personne realisateur = realList.get(0);
		film.setRealisateur(realisateur);
		filmService.updateFilm(film);
		Film filmUpdated = filmService.findFilm(film.getId());
		assertNotNull(filmUpdated);
		assertEquals(StringUtils.upperCase(FilmUtils.TITRE_FILM_UPDATED), filmUpdated.getTitre());
		assertEquals(realisateur, filmUpdated.getRealisateur());
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
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		Integer id = filmService.saveNewFilm(film);
		assertNotNull(id);
		film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(film);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(FilmUtils.TITRE_FILM, 5),null,null,null,null);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f : films){
			logger.debug(f.toString());
		}
		assertEquals(1, films.size());
		assertEquals(StringUtils.upperCase(FilmUtils.TITRE_FILM),films.get(0).getTitre());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaActeursService() {
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		Integer id = filmService.saveNewFilm(film);
		assertNotNull(id);
		Film f = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(f);
		assertNotNull(f.getActeurs().iterator());
		Integer selectedActeurId = f.getActeurs().iterator().next().getId();
		logger.debug("selectedActeurId="+selectedActeurId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,selectedActeurId,null);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(2, films.size());
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
		
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
		Integer id = filmService.saveNewFilm(film);
		assertNotNull(id);
		Film f = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(f);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto("Lorem",null,null,null,null);
		List<Film> films = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(1, films.size());
		Film f2 = films.get(0);
		assertEquals(StringUtils.upperCase(FilmUtils.TITRE_FILM),f2.getTitre());
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
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
			Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
			film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM,2015,idRealisateur,idActeur1,null,null);
			id = filmService.saveNewFilm(film);
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		}else {
			film = filmService.findFilm(id);
		}
		assertNotNull(film);
		assertNotNull(film.getId());
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
