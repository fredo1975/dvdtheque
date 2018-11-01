package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.SortedSet;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.dto.FilmDto;
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

	private FilmDto createNewFilm() {
		FilmDto film = filmService.saveNewFilm(FilmTestUtils.buildFilmDto("Titre"));
		assertNotNull(film);
		return film;
	}
	@Test
	public void findPersonneGetVersusLoad() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		if(id==null) {
			// insert a personne first
			PersonneDto pToInsert = personneService.savePersonne(new PersonneDto("toto", "titi"));
			assertNotNull(pToInsert);
			id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		logger.debug("personneByLoad=" + personneByLoad.toString());
	}

	@Test
	public void findPersonne() throws Exception {
		String methodName = "findPersonne : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		if(id==null) {
			// insert a personne first
			PersonneDto pToInsert = personneService.savePersonne(new PersonneDto("toto", "titi"));
			assertNotNull(pToInsert);
			id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		PersonneDto personneDto = personneService.findByPersonneId(id);
		assertNotNull(personneDto);
		logger.debug(methodName + "personneDto=" + personneDto.toString());
		logger.debug(methodName + "end");
	}

	@Test
	public void findAllRealisateurs() {
		FilmDto film = createNewFilm();
		assertNotNull(film);
		List<PersonneDto> realList = personneService.findAllRealisateur();
		assertNotNull(realList);
	}
	@Test
	public void findAllActeurs() {
		FilmDto film = createNewFilm();
		assertNotNull(film);
		List<PersonneDto> actList = personneService.findAllActeur();
		assertNotNull(actList);
	}
	@Test
	public void findRealisateurByFilm() throws Exception {
		String methodName = "findRealisateurByFilm : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_FILM_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = createNewFilm();
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto film = filmService.findFilm(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.debug(methodName + "film =" + film.toString());
		RealisateurDto real = personneService.findRealisateurByFilm(film);
		assertNotNull(real);
		logger.debug(methodName + "real = " + real.toString());
		logger.debug(methodName + "end");
	}

	@Test
	public void findAllPersonne() throws Exception {
		String methodName = "findAllPersonne : ";
		logger.debug(methodName + "start");
		List<PersonneDto> personneList = personneService.findAllPersonne();
		assertNotNull(personneList);
		for (PersonneDto personne : personneList) {
			personneService.findByPersonneId(personne.getId());
			// logger.info(methodName + "personne="+personne.toString());
		}
		logger.debug(methodName + "personneList.size() = " + personneList.size());
		logger.debug(methodName + "end");
	}

	@Test
	public void findPersonneByFullName() throws Exception {
		String methodName = "findPersonneByFullName : ";
		logger.debug(methodName + "start");
		// insert a personne first
		PersonneDto pToInsert = new PersonneDto(FilmTestUtils.ACT_NOM, FilmTestUtils.ACT_PRENOM);
		pToInsert = personneService.savePersonne(pToInsert);
		assertNotNull(pToInsert);
		PersonneDto pDto = personneService.findPersonneByFullName(FilmTestUtils.ACT_NOM, FilmTestUtils.ACT_PRENOM);
		assertNotNull(pDto);
		logger.debug(methodName + "pDto = " + pDto.toString());
		logger.debug(methodName + "end");
	}

	
	@Test
	public void findAllPersonneByFilm() throws Exception {
		String methodName = "findAllPersonneByFilm : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_FILM_ID_SQL, Integer.class);
		if(id==null) {
			FilmDto film = createNewFilm();
			assertNotNull(film);
			id = film.getId();
		}
		FilmDto filmDto = filmService.findFilm(id);
		assertNotNull(filmDto);
		assertNotNull(filmDto.getTitre());
		logger.debug(methodName + "filmDto =" + filmDto.toString());
		PersonnesFilm personnesFilm = personneService.findAllPersonneByFilm(filmDto);
		assertNotNull(personnesFilm);

		logger.debug(methodName + "personnesFilm = " + personnesFilm.toString());
		logger.debug(methodName + "end");
	}

	
	@Test
	public void savePersonne() throws Exception {
		String methodName = "savePersonne : ";
		logger.debug(methodName + "start");
		String prenom = "fredo";
		String nom = "elbedo";
		PersonneDto personneDto = FilmTestUtils.buildPersonneDto(prenom, nom);
		PersonneDto resultPersonneDto = personneService.savePersonne(personneDto);
		assertNotNull(resultPersonneDto);
		logger.debug(methodName + "resultPersonneDto = " + resultPersonneDto.toString());
		logger.debug(methodName + "end");
	}

	@Test
	public void updatePersonne() throws Exception {
		String methodName = "updatePersonne : ";
		logger.debug(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		if(id==null) {
			// insert a personne first
			PersonneDto pToInsert = personneService.savePersonne(new PersonneDto("toto", "titi"));
			assertNotNull(pToInsert);
			id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		PersonneDto personneDto = PersonneDto.toDto(personneByLoad);
		personneDto.setNom("elmafioso33");
		personneService.updatePersonne(personneDto);
		logger.debug(methodName + "personneDto = " + personneDto.toString());
		logger.debug(methodName + "end");
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
		personneService.deletePersonne(personneDto);
		logger.debug(methodName + "end");
	}
}
