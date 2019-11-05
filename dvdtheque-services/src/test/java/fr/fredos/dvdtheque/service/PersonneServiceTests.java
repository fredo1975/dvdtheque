package fr.fredos.dvdtheque.service;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class})
public class PersonneServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceTests.class);
	@Autowired
	protected IPersonneService personneService;
	@Autowired
	protected IFilmService filmService;
	
	private void assertFilmIsNotNull(Film film) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		assertTrue(CollectionUtils.isNotEmpty(film.getGenres()));
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()==3);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
	}
	@Test
	public void getPersonneVersusLoadPersonne() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		Personne personneByLoad = personneService.loadPersonne(film.getRealisateurs().iterator().next().getId());
		assertNotNull(personneByLoad);
		logger.debug("personneByLoad=" + personneByLoad.toString());
	}
	@Test
	public void findPersonne() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		Personne personne = personneService.findByPersonneId(film.getRealisateurs().iterator().next().getId());
		assertNotNull(personne);
	}
	@Test
	public void findAllRealisateurs() {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		List<Personne> realList = personneService.findAllRealisateur();
		assertNotNull(realList);
		assertTrue(CollectionUtils.isNotEmpty(realList));
		assertTrue(realList.size()==1);
	}
	@Test
	public void findAllActeurs() {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		List<Personne> actList = personneService.findAllActeur();
		assertNotNull(actList);
		assertTrue(CollectionUtils.isNotEmpty(actList));
		assertTrue(actList.size()==3);
	}
	@Test
	public void findRealisateurByFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		film = filmService.findFilm(film.getId());
		assertNotNull(film);
		assertNotNull(film.getTitre());
		Personne real = personneService.findRealisateurByFilm(film);
		assertNotNull(real);
	}
	@Test
	public void findAllPersonnes() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		List<Personne> personneList = personneService.findAllPersonne();
		assertNotNull(personneList);
		assertTrue(CollectionUtils.isNotEmpty(personneList));
		assertTrue(personneList.size()==4);
		for (Personne personne : personneList) {
			Personne p = personneService.findByPersonneId(personne.getId());
			assertNotNull(p);
		}
		Film film2 = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM_UPDATED, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film2);
		List<Personne> personne2List = personneService.findAllPersonne();
		assertNotNull(personne2List);
		assertTrue(CollectionUtils.isNotEmpty(personne2List));
		assertTrue(personne2List.size()==4);
		for (Personne personne : personne2List) {
			Personne p = personneService.findByPersonneId(personne.getId());
			assertNotNull(p);
		}
	}
	@Test
	public void findPersonneByFullName() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		Personne personne = personneService.findPersonneByName(FilmServiceTests.ACT1_NOM);
		assertNotNull(personne);
	}
	@Test
	public void findAllPersonneByFilm() throws Exception {
		Film film = filmService.createOrRetrieveFilm(FilmServiceTests.TITRE_FILM, 
				FilmServiceTests.ANNEE,
				FilmServiceTests.REAL_NOM,
				FilmServiceTests.ACT1_NOM,
				FilmServiceTests.ACT2_NOM,
				FilmServiceTests.ACT3_NOM, null, null, new Genre(28,"Action"),new Genre(35,"Comedy"));
		assertFilmIsNotNull(film);
		film = filmService.findFilm(film.getId());
		assertFilmIsNotNull(film);
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
		Long maxPersonneId = this.jdbcTemplate.queryForObject("select max(ID) from Personne", Long.class);
		// PersonneDto personneDto = personneService.findByPersonneId(maxPersonneId);
		PersonneDto personneDto = new PersonneDto();
		personneDto.setId(maxPersonneId);
		//personneService.deletePersonne(personneDto);
		logger.debug(methodName + "end");
	}
}
