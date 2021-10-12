package fr.fredos.dvdtheque.dao.model.repository;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.impl.PersonneDaoImpl;

@ExtendWith(SpringExtension.class)
@Import(PersonneDaoImpl.class)
@DataJpaTest
@ActiveProfiles("test")
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
