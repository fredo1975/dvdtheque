package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.service.dto.ActeurDto;
import fr.fredos.dvdtheque.service.dto.FilmDto;
import fr.fredos.dvdtheque.service.dto.FilmUtils;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
import fr.fredos.dvdtheque.service.dto.PersonnesFilm;
import fr.fredos.dvdtheque.service.dto.RealisateurDto;

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
		String titre = "Titre";
		Film film = FilmUtils.buildFilm(titre);
		filmService.saveNewFilm(film);
		film = filmService.findFilmByTitre(titre);
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
			filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		}else {
			film = filmService.findFilm(id);
		}
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug(methodName + "film ="+film.toString());
		assertNotNull(film.getActeurs());
		assertNotNull(film.getRealisateurs());
		assertNotNull(film.getRealisateur());
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
			filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
			film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		}else {
			film = filmService.findFilm(id);
		}
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug("film ="+film.toString());
		assertNotNull(film.getRealisateurs());
		assertNotNull(film.getRealisateur());
		logger.debug("Realisateur ="+film.getRealisateur().toString());
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
		Personne acteur = FilmUtils.buildPersonne(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM);
		personneService.savePersonne(acteur);
		acteur = personneService.findPersonneByFullName(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM);
		assertNotNull(acteur);
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
		film.getActeurs().add(acteur);
		filmService.saveNewFilm(film);
		logger.debug(methodName + "end");
	}
	@Test
	@Transactional
	public void saveNewFilmWithExistingPersons() {
		String methodName = "saveNewFilmWithExistingPersons : ";
		logger.debug(methodName + "start");
		
		Film film = FilmUtils.buildFilm(FilmUtils.TITRE_FILM);
		filmService.saveNewFilm(film);
		film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		film = filmService.findFilmWithAllObjectGraph(film.getId());
		assertNotNull(film);
		assertNotNull(film.getTitre());
		assertEquals(FilmUtils.TITRE_FILM, film.getTitre());
		assertNotNull(film.getRealisateur().getId());
		Integer idReal = film.getRealisateur().getId();
		assertNotNull(idReal);
		Personne realisateur = personneService.findByPersonneId(idReal);
		assertNotNull(realisateur);
		assertNotNull(film.getActeurs().iterator());
		Integer idAct = film.getActeurs().iterator().next().getId();
		assertNotNull(idAct);
		Personne acteur = personneService.findByPersonneId(idAct);
		
		Dvd dvd = new Dvd();
		dvd.setZone(new Integer(2));
		dvd.setAnnee(2002);
		dvd.setEdition("edition");
		Film newFilm = new Film();
		newFilm.setTitre("test1");
		newFilm.setAnnee(2002);
		newFilm.setDvd(dvd);
		newFilm.getRealisateurs().add(realisateur);
		newFilm.getActeurs().add(acteur);
		
		filmService.saveNewFilm(newFilm);
		Film filmUpdated = filmService.findFilm(newFilm.getId());
		assertNotNull(filmUpdated);
		logger.debug(methodName + "end");
	}
	@Test
	@Transactional
	public void updateFilm(){
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
		}else {
			film = filmService.findFilm(id);
		}
		assertNotNull(film);
		film.setTitre(FilmUtils.TITRE_FILM);
		filmService.updateFilm(film);
		Film filmUpdated = filmService.findFilm(id);
		assertNotNull(filmUpdated);
		assertEquals(FilmUtils.TITRE_FILM, filmUpdated.getTitre());
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
		filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
		Film film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(film);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(FilmUtils.TITRE_FILM, 5),null,null,null,null);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film f : films){
			logger.debug(f.toString());
		}
		assertEquals(1, films.size());
		assertEquals(FilmUtils.TITRE_FILM,films.get(0).getTitre());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaActeursService() {
		filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
		Film f = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(f);
		assertNotNull(f.getActeurs().iterator());
		Integer selectedActeurId = f.getActeurs().iterator().next().getId();
		logger.debug("selectedActeurId="+selectedActeurId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,selectedActeurId,null);
		List<Film> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film film : films){
			logger.debug(film.toString());
		}
		assertEquals(1, films.size());
		Film film = films.get(0);
		assertNotNull(film);
		assertNotNull(film.getActeurs());
		Optional<Personne> op = film.getActeurs().stream().filter(acteurDto -> acteurDto.getId().equals(selectedActeurId)).findAny();
		Personne acteur = op.get();
		assertNotNull(acteur);
		assertEquals(selectedActeurId, acteur.getId());
	}
	@Test
	public void findAllFilmsByCriteriaDao() {
		String methodName = "findAllFilmsByCriteria : ";
		logger.debug(methodName + "start");
		
		filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
		Film f = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(f);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto("Lorem",null,null,null,null);
		List<Film> films = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film film : films){
			logger.debug(film.toString());
		}
		assertEquals(1, films.size());
		Film film = films.get(0);
		assertEquals(FilmUtils.TITRE_FILM,film.getTitre());
		Set<Personne> realisateurSet = film.getRealisateurs();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		assertEquals(FilmUtils.REAL_NOM,realisateur.getNom());
		assertEquals(FilmUtils.REAL_PRENOM,realisateur.getPrenom());
		logger.debug(methodName + "end");
	}
	@Test
	public void removeFilmDao() {
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		Film film = null;
		if(id==null) {
			filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
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
