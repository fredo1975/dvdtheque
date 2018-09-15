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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.dto.ActeurDto;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.dto.PersonnesFilm;
import fr.fredos.dvdtheque.dto.RealisateurDto;

@ContextConfiguration(locations={"classpath:business-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class FilmServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	protected FilmDao filmDao;
	protected Logger logger = LoggerFactory.getLogger(FilmServiceTests.class);
	@Autowired
	protected FilmService filmService;
	@Autowired
	protected PersonneService personneService;
	private final static String MAX_ID_SQL = "select max(id) from FILM";
	
	@Test
	public void findFilmByTitre() throws Exception{
		String methodName = "findFilmByTitre : ";
		logger.info(methodName + "start");
		String titre = "Titre";
		FilmDto film = FilmTestUtils.buildFilmDto(titre);
		film = filmService.saveNewFilm(film);
		film = filmService.findFilmByTitre(titre);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		
		//FilmDto filmNotFound = filmService.findFilmByTitre("blade runne");
		logger.info(methodName + "end");
	}
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.info(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilmWithAllObjectGraph(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.info(methodName + "film ="+film.toString());
		assertNotNull(film.getPersonnesFilm().getActeurs());
		for(ActeurDto acteur : film.getPersonnesFilm().getActeurs()){
			logger.info(methodName + " acteur="+acteur.toString());
		}
		logger.info(methodName + "end");
	}
	@Test
	public void findFilm() throws Exception {
		String methodName = "findFilm : ";
		logger.info(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilm(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.info(methodName + "film ="+film.toString());
		assertNotNull(film.getPersonnesFilm().getRealisateur());
		logger.info(methodName + "Realisateur ="+film.getPersonnesFilm().getRealisateur().toString());
		
		assertNotNull(film.getPersonnesFilm().getActeurs());
		for(ActeurDto acteur : film.getPersonnesFilm().getActeurs()){
			logger.info(methodName + " acteur="+acteur.toString());
		}
		logger.info(methodName + "end");
	}
	@Test
	public void findAllFilm() throws Exception {
		String methodName = "findAllFilm : ";
		logger.info(methodName + "start");
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
		logger.info(watch.prettyPrint());
		logger.info(methodName + "end");
	}
	@Test
	public void findAllRippedFilm() throws Exception {
		String methodName = "findAllRippedFilm : ";
		logger.info(methodName + "start");
		List<Film> films = filmService.getAllRippedFilms();
		logger.info(methodName + "######### size ="+films.size());
		for(Film film : films){
			assertNotNull(film);
			assertNotNull(film.getTitre());
			//logger.info(methodName + "######### film ="+film.toString());
		}
		logger.info(methodName + "end");
	}
	@Test
	public void getAllFilmDtos() {
		String methodName = "getAllFilm : ";
		logger.info(methodName + "start");
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
		logger.info(watch.prettyPrint());
		
		StopWatch cachedWatch = new StopWatch();
		cachedWatch.start();
		films = filmService.getAllFilmDtos();
		cachedWatch.stop();
		logger.info(cachedWatch.prettyPrint());
		
		StopWatch cachedWatch2 = new StopWatch();
		cachedWatch2.start();
		films = filmService.getAllFilmDtos();
		cachedWatch2.stop();
		logger.info(cachedWatch2.prettyPrint());
		
		logger.info(methodName + "end");
	}
	@Test
	public void getAllFilm() {
		String methodName = "getAllFilm : ";
		logger.info(methodName + "start");
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
		logger.info(watch.prettyPrint());
		logger.info(methodName + "end");
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
		logger.info(methodName + "start");
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
		logger.info(methodName + "end");
	}
	@Test
	@Transactional
	public void saveNewFilmWithExistingPersons() {
		String methodName = "saveNewFilmWithExistingPersons : ";
		logger.info(methodName + "start");
		
		FilmDto f = FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM);
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
		logger.info(methodName + "end");
	}
	@Test
	@Transactional
	public void updateFilm(){
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilm(id);
		assertNotNull(film);
		logger.info("film="+film.toString());
		film.setTitre("fake titre");
		ActeurDto ac = null;
		for(ActeurDto p : film.getPersonnesFilm().getActeurs()){
			logger.info("p.getPersonne().toString()="+p.getPersonne().toString());
			ac = p;
		}
		film.getPersonnesFilm().getActeurs().remove(ac);
		Film f = film.fromDto();
		filmService.updateFilm(f);
		
	}
	@Test
	public void getAllFilmsByDao(){
		String methodName = "getAllFilmsByDao : ";
		logger.info(methodName + "start");
		List<Film> films = filmDao.findAllFilms();
		assertNotNull(films);
		
		for(Film film : films){
			logger.info(methodName + "film = "+film.toString());
			Set<Personne> realisateurs = film.getRealisateurs();
			for(Personne real : realisateurs){
				logger.info(methodName + "real = "+real.toString());
			}
		}
		logger.info(methodName + "films.size() = "+films.size());
		logger.info(methodName + "end");
	}
	@Test
	public void getAllFilmsByService(){
		String methodName = "getAllFilmsByService : ";
		logger.info(methodName + "start");
		List<Film> films = filmService.getAllFilms();
		assertNotNull(films);
		
		for(Film film : films){
			logger.info(methodName + "film = "+film.toString());
			Set<Personne> realisateurs = film.getRealisateurs();
			for(Personne real : realisateurs){
				logger.info(methodName + "real = "+real.toString());
			}
		}
		logger.info(methodName + "films.size() = "+films.size());
		
		List<Film> films2 = filmService.getAllFilms();
		assertNotNull(films2);
		logger.info(methodName + "end");
	}
	@Test
	public void cleanAllFilms() {
		String methodName = "cleanAllFilms : ";
		logger.info(methodName + "start");
		filmService.cleanAllFilms();
		logger.info(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaTtireService() {
		String methodName = "findAllFilmsByCriteriaTtireService : ";
		logger.info(methodName + "start");
		FilmDto f = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
		assertNotNull(f);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(StringUtils.left(FilmTestUtils.TITRE_FILM, 5),null,null,null,null);
		List<FilmDto> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(FilmDto film : films){
			logger.info(film.toString());
		}
		assertEquals(1, films.size());
		assertEquals(FilmTestUtils.TITRE_FILM,films.get(0).getTitre());
		logger.info(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaActeursService() {
		String methodName = "findAllFilmsByCriteriaActeursService : ";
		logger.info(methodName + "start");
		FilmDto f = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
		assertNotNull(f);
		assertNotNull(f.getPersonnesFilm().getActeurs().iterator());
		Integer selectedActeurId = f.getPersonnesFilm().getActeurs().iterator().next().getPersonne().getId();
		logger.info("selectedActeurId="+selectedActeurId);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(null,null,null,selectedActeurId,null);
		List<FilmDto> films = filmService.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(FilmDto film : films){
			logger.info(film.toString());
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
		logger.info(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaDao() {
		String methodName = "findAllFilmsByCriteria : ";
		logger.info(methodName + "start");
		
		FilmDto f = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
		assertNotNull(f);
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto("Lorem",null,null,null,null);
		List<Film> films = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		assertNotNull(films);
		for(Film film : films){
			logger.info(film.toString());
		}
		assertEquals(1, films.size());
		Film film = films.get(0);
		assertEquals(FilmTestUtils.TITRE_FILM,film.getTitre());
		Set<Personne> realisateurSet = film.getRealisateurs();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		assertEquals(FilmTestUtils.REAL_NOM,realisateur.getNom());
		assertEquals(FilmTestUtils.REAL_PRENOM,realisateur.getPrenom());
		logger.info(methodName + "end");
	}
	@Test
	public void removeFilmDao() {
		Integer id = this.jdbcTemplate.queryForObject(MAX_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
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
			FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto(FilmTestUtils.TITRE_FILM));
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
