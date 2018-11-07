package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.dto.FilmUtils;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
import fr.fredos.dvdtheque.service.dto.PersonnesFilm;
import fr.fredos.dvdtheque.service.dto.RealisateurDto;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})
public class PersonneServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceTests.class);
	@Autowired
	protected PersonneService personneService;
	@Autowired
	protected FilmService filmService;

	public static final String CACHE_PERSONNE = "repl-personne";
	public static final String CACHE_FILM = "dist-film";
	public final static String MAX_FILM_ID_SQL = "select max(id) from FILM";
	public final static String MAX_PERSONNE_ID_SQL = "select max(id) from PERSONNE";

	private Film createNewFilm() {
		filmService.saveNewFilm(FilmUtils.buildFilm(FilmUtils.TITRE_FILM));
		Film film = filmService.findFilmByTitre(FilmUtils.TITRE_FILM);
		assertNotNull(film);
		return film;
	}
	@Test
	public void getPersonneVersusLoadPersonne() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		if(id==null) {
			// insert a personne first
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM));
			
			id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		logger.debug("personneByLoad=" + personneByLoad.toString());
	}

	@Test
	public void findPersonne() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		if(id==null) {
			// insert a personne first
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM));
			id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Personne personne = personneService.findByPersonneId(id);
		assertNotNull(personne);
		
	}

	@Test
	public void findAllRealisateurs() {
		Film film = createNewFilm();
		assertNotNull(film);
		List<Personne> realList = personneService.findAllRealisateur();
		assertNotNull(realList);
	}
	@Test
	public void findAllActeurs() {
		Film film = createNewFilm();
		assertNotNull(film);
		List<Personne> actList = personneService.findAllActeur();
		assertNotNull(actList);
	}
	@Test
	public void findRealisateurByFilm() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(MAX_FILM_ID_SQL, Integer.class);
		if(id==null) {
			Film film = createNewFilm();
			assertNotNull(film);
			id = film.getId();
		}
		Film film = filmService.findFilm(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		Personne real = personneService.findRealisateurByFilm(film);
		assertNotNull(real);
	}

	@Test
	public void findAllPersonne() throws Exception {
		List<Personne> personneList = personneService.findAllPersonne();
		assertNotNull(personneList);
		for (Personne personne : personneList) {
			personneService.findByPersonneId(personne.getId());
		}
	}

	@Test
	public void findPersonneByFullName() throws Exception {
		// insert a personne first
		personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM));
		Personne personne = personneService.findPersonneByFullName(FilmUtils.ACT1_NOM, FilmUtils.ACT1_PRENOM);
		assertNotNull(personne);
	}

	
	@Test
	public void findAllPersonneByFilm() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(MAX_FILM_ID_SQL, Integer.class);
		if(id==null) {
			Film film = createNewFilm();
			assertNotNull(film);
			id = film.getId();
		}
		Film film = filmService.findFilm(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		
	}

	
	@Test
	public void savePersonne() throws Exception {
		Personne personne = FilmUtils.buildPersonne(FilmUtils.ACT1_NOM, FilmUtils.ACT1_PRENOM);
		personneService.savePersonne(personne);
		personne = personneService.findPersonneByFullName(FilmUtils.ACT1_NOM, FilmUtils.ACT1_PRENOM);
		assertNotNull(personne);
	}

	@Test
	public void updatePersonne() throws Exception {
		String methodName = "updatePersonne : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		if(id==null) {
			// insert a personne first
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM, FilmUtils.ACT1_PRENOM));
			id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		personneByLoad.setNom("elmafioso33");
		personneService.updatePersonne(personneByLoad);
	}

	@Test
	public void cleanAllPersonne() throws Exception {
		String methodName = "cleanAllPersonne : ";
		logger.debug(methodName + "start");
		personneService.cleanAllPersonnes();
		logger.debug(methodName + "end");
	}

	@Test
	@Ignore
	public void deletePersonne() throws Exception {
		String methodName = "deletePersonne : ";
		logger.debug(methodName + "start");
		Integer maxPersonneId = this.jdbcTemplate.queryForObject("select max(ID) from Personne", Integer.class);
		// PersonneDto personneDto = personneService.findByPersonneId(maxPersonneId);
		PersonneDto personneDto = new PersonneDto();
		personneDto.setId(maxPersonneId);
		//personneService.deletePersonne(personneDto);
		logger.debug(methodName + "end");
	}
}
