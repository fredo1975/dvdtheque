package fr.fredos.dvdtheque.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.dto.PersonnesFilm;
import fr.fredos.dvdtheque.dto.RealisateurDto;

@ContextConfiguration(locations={"classpath:business-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
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
	
	@Test
	public void findPersonneGetVersusLoad() throws Exception {
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		logger.info("personneByLoad="+personneByLoad.toString());
	}
	@Test
	public void findPersonne() throws Exception {
		String methodName = "findPersonne : ";
		logger.info(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		PersonneDto personneDto = personneService.findByPersonneId(id);
		assertNotNull(personneDto);
		logger.info(methodName + "personneDto="+personneDto.toString());
		logger.info(methodName + "end");
	}
	
	@Test
	public void findRealisateurByFilm() throws Exception {
		String methodName = "findRealisateurByFilm : ";
		logger.info(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_FILM_ID_SQL, Integer.class);
		FilmDto film = filmService.findFilm(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.info(methodName + "film ="+film.toString());
		RealisateurDto real = personneService.findRealisateurByFilm(film);
		assertNotNull(real);
		logger.info(methodName + "real = "+real.toString());
		logger.info(methodName + "end");
	}
	
	@Test
	public void findAllPersonne() throws Exception {
		String methodName = "findAllPersonne : ";
		logger.info(methodName + "start");
		List<PersonneDto> personneList = personneService.findAllPersonne();
		assertNotNull(personneList);
		for(PersonneDto personne : personneList){
			personneService.findByPersonneId(personne.getId());
			//logger.info(methodName + "personne="+personne.toString());
		}
		logger.info(methodName + "personneList.size() = "+personneList.size());
		logger.info(methodName + "end");
	}
	
	@Test
	public void findPersonneByFullName() throws Exception {
		String methodName = "findPersonneByFullName : ";
		logger.info(methodName + "start");
		String nom = "allen";
		String prenom = "woody";
		PersonneDto pDto = personneService.findPersonneByFullName(nom, prenom);
		assertNotNull(pDto);
		logger.info(methodName + "pDto = "+pDto.toString());
		logger.info(methodName + "end");
	}
	@Test
	public void findAllPersonneByFilm() throws Exception {
		String methodName = "findAllPersonneByFilm : ";
		logger.info(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_FILM_ID_SQL, Integer.class);
		FilmDto film = filmService.findFilm(id);
		assertNotNull(film);
		assertNotNull(film.getTitre());
		logger.info(methodName + "film ="+film.toString());
		PersonnesFilm personnesFilm = personneService.findAllPersonneByFilm(film);
		assertNotNull(personnesFilm);
		
		logger.info(methodName + "personnesFilm = "+personnesFilm.toString());
		logger.info(methodName + "end");
	}
	private PersonneDto buildPersonneDto(String prenom,String nom){
		PersonneDto p = new PersonneDto();
		p.setNom(nom);
		p.setPrenom(prenom);
		return p;
	}
	
	@Test
	public void savePersonne() throws Exception {
		String methodName = "savePersonne : ";
		logger.info(methodName + "start");
		String prenom = "fredo";
		String nom = "elbedo";
		PersonneDto personneDto = buildPersonneDto(prenom,nom);
		PersonneDto resultPersonneDto = personneService.savePersonne(personneDto);
		assertNotNull(resultPersonneDto);
		logger.info(methodName + "resultPersonneDto = "+resultPersonneDto.toString());
		logger.info(methodName + "end");
	}
	
	@Test
	public void updatePersonne() throws Exception {
		String methodName = "updatePersonne : ";
		logger.info(methodName + "start");
		Integer id = this.jdbcTemplate.queryForObject(MAX_PERSONNE_ID_SQL, Integer.class);
		Personne personneByLoad = personneService.loadPersonne(id);
		assertNotNull(personneByLoad);
		PersonneDto personneDto = PersonneDto.toDto(personneByLoad);
		personneDto.setNom("elmafioso33");
		personneService.updatePersonne(personneDto);
		logger.info(methodName + "personneDto = "+personneDto.toString());
		logger.info(methodName + "end");
	}
	
	@Test
	public void cleanAllPersonne() throws Exception {
		String methodName = "cleanAllPersonne : ";
		logger.info(methodName + "start");
		personneService.cleanAllPersonnes();
		logger.info(methodName + "end");
	}
	
	@Test
	@Ignore
	public void deletePersonne() throws Exception {
		String methodName = "deletePersonne : ";
		logger.info(methodName + "start");
		Integer maxPersonneId = this.jdbcTemplate.queryForObject("select max(ID) from Personne", Integer.class);
		//PersonneDto personneDto = personneService.findByPersonneId(maxPersonneId);
		PersonneDto personneDto = new PersonneDto();
		personneDto.setId(maxPersonneId);
		personneService.deletePersonne(personneDto);
		logger.info(methodName + "end");
	}
}
