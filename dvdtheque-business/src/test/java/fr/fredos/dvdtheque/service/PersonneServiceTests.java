package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import fr.fredos.dvdtheque.service.dto.PersonneDto;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})
public class PersonneServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceTests.class);
	@Autowired
	protected IPersonneService personneService;
	@Autowired
	protected IFilmService filmService;

	public static final String CACHE_PERSONNE = "repl-personne";
	public static final String CACHE_FILM = "dist-film";
	public final static String MAX_FILM_ID_SQL = "select max(id) from FILM";
	public final static String MAX_PERSONNE_ID_SQL = "select max(id) from PERSONNE";
	public final static String MAX_REALISATEUR_ID_SQL = "select max(p.id) from PERSONNE p inner join REALISATEUR r on r.ID_PERSONNE=p.ID";
	public final static String MAX_ACTEUR_ID_SQL = "select max(p.id) from PERSONNE p inner join ACTEUR a on a.ID_PERSONNE=p.ID";

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
	public void getPersonneVersusLoadPersonne() throws Exception {
		Integer id = personneService.savePersonne(personneService.buildPersonne(FilmServiceTests.ACT1_NOM));
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		logger.debug("personneByLoad=" + personneByLoad.toString());
	}

	@Test
	public void findPersonne() throws Exception {
		Integer id = personneService.savePersonne(personneService.buildPersonne(FilmServiceTests.ACT1_NOM));
		Personne personne = personneService.findByPersonneId(id);
		assertNotNull(personne);
	}

	@Test
	public void findAllRealisateurs() {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM);
		assertFilmIsNotNull(film);
		List<Personne> realList = personneService.findAllRealisateur();
		assertNotNull(realList);
	}
	@Test
	public void findAllActeurs() {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM);
		assertFilmIsNotNull(film);
		List<Personne> actList = personneService.findAllActeur();
		assertNotNull(actList);
	}
	@Test
	public void findRealisateurByFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM);
		assertFilmIsNotNull(film);
		film = filmService.findFilm(film.getId());
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
			Personne p = personneService.findByPersonneId(personne.getId());
			assertNotNull(p);
		}
	}

	@Test
	public void findPersonneByFullName() throws Exception {
		// insert a personne first
		Integer id = personneService.savePersonne(personneService.buildPersonne(FilmServiceTests.ACT1_NOM));
		Personne personne = personneService.findPersonneByFullName(FilmServiceTests.ACT1_NOM);
		assertNotNull(personne);
	}

	
	@Test
	public void findAllPersonneByFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM);
		assertFilmIsNotNull(film);
		film = filmService.findFilm(film.getId());
		assertFilmIsNotNull(film);
	}

	
	@Test
	public void savePersonne() throws Exception {
		Personne personne = personneService.buildPersonne(FilmServiceTests.ACT1_NOM);
		Integer id = personneService.savePersonne(personne);
		assertNotNull(id);
		personne.setId(id);
		personne = personneService.findPersonneByFullName(FilmServiceTests.ACT1_NOM);
		assertNotNull(personne);
	}

	@Test
	public void updatePersonne() throws Exception {
		String methodName = "updatePersonne : ";
		logger.debug(methodName + "start");
		Personne personne = personneService.buildPersonne(FilmServiceTests.ACT1_NOM);
		Integer id = personneService.savePersonne(personne);
		assertNotNull(id);
		personne.setId(id);
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		personneByLoad.setNom(FilmServiceTests.ACT2_NOM);
		personneService.updatePersonne(personneByLoad);
		assertEquals(FilmServiceTests.ACT2_NOM, personneByLoad.getNom());
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
