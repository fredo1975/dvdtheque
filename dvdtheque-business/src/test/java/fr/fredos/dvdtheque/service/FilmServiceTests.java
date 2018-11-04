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
		FilmDto film = FilmUtils.buildFilmDto(titre);
		film = filmService.saveNewFilm(film);
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
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilmWithAllObjectGraph(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug(methodName + "film ="+film.toString());
		assertNotNull(film.getPersonnesFilm().getActeurs());
		for(ActeurDto acteur : film.getPersonnesFilm().getActeurs()){
			logger.debug(methodName + " acteur="+acteur.toString());
		}
		logger.debug(methodName + "end");
	}
	@Test
	public void findFilm() throws Exception {
		String methodName = "findFilm : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilm(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug(methodName + "film ="+film.toString());
		assertNotNull(film.getPersonnesFilm().getRealisateur());
		logger.debug(methodName + "Realisateur ="+film.getPersonnesFilm().getRealisateur().toString());
		
		assertNotNull(film.getPersonnesFilm().getActeurs());
		for(ActeurDto acteur : film.getPersonnesFilm().getActeurs()){
			logger.debug(methodName + " acteur="+acteur.toString());
		}
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilm() throws Exception {
		String methodName = "findAllFilm : ";
		logger.debug(methodName + "start");
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = filmService.findAllFilms();
		/*
		for(Film film : films){
			assertNotNull(film);
			assertNotNull(film.getTitre());
			logger.info(methodName + "film ="+film.toString());
		}
		*/
		assertNotNull(films);
		watch.stop();
		logger.debug(watch.prettyPrint());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllRippedFilm() throws Exception {
		String methodName = "findAllRippedFilm : ";
		logger.debug(methodName + "start");
		List<Film> films = filmService.getAllRippedFilms();
		logger.info(methodName + "######### size ="+films.size());
		for(Film film : films){
			assertNotNull(film);
			assertNotNull(film.getTitre());
			//logger.info(methodName + "######### film ="+film.toString());
		}
		logger.debug(methodName + "end");
	}
	@Test
	public void getAllFilmDtos() {
		String methodName = "getAllFilm : ";
		logger.debug(methodName + "start");
		StopWatch watch = new StopWatch();
		watch.start();
		List<FilmDto> films = filmService.getAllFilmDtos();
		assertNotNull(films);
		for(FilmDto film : films){
			assertNotNull(film);
			assertNotNull(film.getTitre());
			//logger.info(methodName + "film ="+film.toString());
			//logger.info(methodName + "Realisateur ="+film.getPrintRealisateur());
		}
		watch.stop();
		logger.debug(watch.prettyPrint());
		
		StopWatch cachedWatch = new StopWatch();
		cachedWatch.start();
		films = filmService.getAllFilmDtos();
		cachedWatch.stop();
		logger.debug(cachedWatch.prettyPrint());
		
		StopWatch cachedWatch2 = new StopWatch();
		cachedWatch2.start();
		films = filmService.getAllFilmDtos();
		cachedWatch2.stop();
		logger.debug(cachedWatch2.prettyPrint());
		
		logger.debug(methodName + "end");
	}
	@Test
	public void getAllFilm() {
		String methodName = "getAllFilm : ";
		logger.debug(methodName + "start");
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = filmService.getAllFilms();
		/*
		for(Film film : films){
			assertNotNull(film);
			assertNotNull(film.getTitre());
			logger.info(methodName + "film ="+film.toString());
			logger.info(methodName + "Realisateur ="+film.getRealisateurs().iterator().next());
		}*/
		assertNotNull(films);
		watch.stop();
		logger.debug(watch.prettyPrint());
		logger.debug(methodName + "end");
	}
	private PersonneDto buildPersonneDto(String prenom,String nom){
		PersonneDto p = new PersonneDto();
		p.setNom(nom);
		p.setPrenom(prenom);
		return p;
	}
	
	@Test
	@Transactional
	public void saveNewFilmWithNewPersons() {
		String methodName = "saveNewFilmWithNewPersons : ";
		logger.debug(methodName + "start");
		String prenom = "fredo";
		String nom = "elbedo";
		PersonneDto personneDto = buildPersonneDto(prenom,nom);
		Personne acteur = PersonneDto.fromDto(personneDto);
		acteur = personneService.savePersonne(acteur);
		
		String prenomAct1 = "acteur1";
		String nomAct1 = "acteur1";
		PersonneDto personneAct1Dto = buildPersonneDto(prenomAct1,nomAct1);
		Personne real = PersonneDto.fromDto(personneAct1Dto);
		real = personneService.savePersonne(real);
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
		
		FilmDto f = FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM);
		f = filmService.saveNewFilm(f);
		assertNotNull(f);
		assertNotNull(f.getPersonnesFilm().getRealisateur().getPersonne().getId());
		Integer idReal = f.getPersonnesFilm().getRealisateur().getPersonne().getId();
		assertNotNull(idReal);
		Personne real = new Personne();
		real.setId(idReal);
		assertNotNull(f.getPersonnesFilm().getActeurs().iterator());
		Integer idAct = f.getPersonnesFilm().getActeurs().iterator().next().getPersonne().getId();
		assertNotNull(idAct);
		Personne acteur = new Personne();
		acteur.setId(idAct);
		
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
		Film result = filmService.updateFilm(film);
		assertNotNull(result);
		logger.debug(methodName + "end");
	}
	@Test
	@Transactional
	public void updateFilm(){
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilm(id);
		assertNotNull(film);
		logger.debug("film="+film.toString());
		film.setTitre("fake titre");
		ActeurDto ac = null;
		for(ActeurDto p : film.getPersonnesFilm().getActeurs()){
			logger.debug("p.getPersonne().toString()="+p.getPersonne().toString());
			ac = p;
		}
		film.getPersonnesFilm().getActeurs().remove(ac);
		Film f = film.fromDto();
		filmService.updateFilm(f);
		
	}
	@Test
	public void getAllFilmsByDao(){
		String methodName = "getAllFilmsByDao : ";
		logger.debug(methodName + "start");
		List<Film> films = filmDao.findAllFilms();
		assertNotNull(films);
		
		for(Film film : films){
			logger.debug(methodName + "film = "+film.toString());
			Set<Personne> realisateurs = film.getRealisateurs();
			for(Personne real : realisateurs){
				logger.debug(methodName + "real = "+real.toString());
			}
		}
		logger.debug(methodName + "films.size() = "+films.size());
		logger.debug(methodName + "end");
	}
	@Test
	public void getAllFilmsByService(){
		String methodName = "getAllFilmsByService : ";
		logger.debug(methodName + "start");
		List<Film> films = filmService.getAllFilms();
		assertNotNull(films);
		
		for(Film film : films){
			logger.debug(methodName + "film = "+film.toString());
			Set<Personne> realisateurs = film.getRealisateurs();
			for(Personne real : realisateurs){
				logger.debug(methodName + "real = "+real.toString());
			}
		}
		logger.debug(methodName + "films.size() = "+films.size());
		
		List<Film> films2 = filmService.getAllFilms();
		assertNotNull(films2);
		logger.debug(methodName + "end");
	}
	@Test
	public void cleanAllFilms() {
		String methodName = "cleanAllFilms : ";
		logger.debug(methodName + "start");
		filmService.cleanAllFilms();
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaTtireService() {
		String methodName = "findAllFilmsByCriteriaTtireService : ";
		logger.debug(methodName + "start");
		FilmDto f = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
		assertNotNull(f);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(FilmUtils.TITRE_FILM, 5),null,null,null,null);
		List<FilmDto> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(FilmDto film : films){
			logger.debug(film.toString());
		}
		assertEquals(1, films.size());
		assertEquals(FilmUtils.TITRE_FILM,films.get(0).getTitre());
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaActeursService() {
		String methodName = "findAllFilmsByCriteriaActeursService : ";
		logger.debug(methodName + "start");
		FilmDto f = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
		assertNotNull(f);
		assertNotNull(f.getPersonnesFilm().getActeurs().iterator());
		Integer selectedActeurId = f.getPersonnesFilm().getActeurs().iterator().next().getPersonne().getId();
		logger.debug("selectedActeurId="+selectedActeurId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,selectedActeurId,null);
		List<FilmDto> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(FilmDto film : films){
			logger.debug(film.toString());
		}
		assertEquals(1, films.size());
		FilmDto filmDto = films.get(0);
		assertNotNull(filmDto);
		assertNotNull(filmDto.getPersonnesFilm());
		PersonnesFilm pf = filmDto.getPersonnesFilm();
		Set<ActeurDto> acteurSet = pf.getActeurs();
		assertNotNull(acteurSet);
		Optional<ActeurDto> op = acteurSet.stream().filter(acteurDto -> acteurDto.getPersonne().getId().equals(selectedActeurId)).findAny();
		ActeurDto acteurDto = op.get();
		assertNotNull(acteurDto);
		assertEquals(selectedActeurId, acteurDto.getPersonne().getId());
		//Optional<Integer> acteurIdOptional = Set.stream().filter(films.get(0).getPersonnesFilm().getActeurs() -> x.));
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaDao() {
		String methodName = "findAllFilmsByCriteria : ";
		logger.debug(methodName + "start");
		
		FilmDto f = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
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
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto filmDto = filmService.findFilm(id);
		assertNotNull(filmDto);
		assertNotNull(filmDto.fromDto());
		assertNotNull(filmDto.fromDto().getId());
		Film film = filmDao.findFilm(filmDto.fromDto().getId());
		Personne real = film.getRealisateurs().iterator().next();
		assertNotNull(real);
		filmDao.removeFilm(film);
		PersonneDto realDto = personneService.findByPersonneId(real.getId());
		assertNotNull(realDto);
	}
	@Test(expected = java.lang.Exception.class)
	public void removeFilmService() {
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmUtils.buildFilmDto(FilmUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto filmDto = filmService.findFilm(id);
		assertNotNull(filmDto);
		RealisateurDto real = filmDto.getPersonnesFilm().getRealisateur();
		assertNotNull(real);
		filmService.removeFilm(filmDto);
		FilmDto deletedFilmDto = filmService.findFilm(id);
		assertNull(deletedFilmDto);
		PersonneDto realDto = personneService.findByPersonneId(real.getPersonne().getId());
		assertNotNull(realDto);
	}
}
