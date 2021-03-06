package fr.fredos.dvdtheque.dao.model.repository;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.Application;
import fr.fredos.dvdtheque.dao.model.object.Personne;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PersonneDaoIntegrationTest {
	protected Logger logger = LoggerFactory.getLogger(PersonneDaoIntegrationTest.class);
	@Autowired
    private PersonneDao personneDao;
	@Test
	public void findAllRealisateur(){
		List<Personne> realisateurs = personneDao.findAllRealisateur();
		assertNotNull(realisateurs);
		logger.info("realisateurs.size()="+realisateurs.size());
	}
	@Test
	public void findAllActeur(){
		List<Personne> acteurs = personneDao.findAllActeur();
		assertNotNull(acteurs);
		logger.info("acteurs.size()="+acteurs.size());
	}
	@Test
	public void findAllPersonne(){
		List<Personne> personnes = personneDao.findAllPersonne();
		assertNotNull(personnes);
		logger.info("personnes.size()="+personnes.size());
	}
}
