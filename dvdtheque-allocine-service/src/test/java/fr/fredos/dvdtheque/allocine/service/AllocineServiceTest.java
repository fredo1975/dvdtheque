package fr.fredos.dvdtheque.allocine.service;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.allocine.domain.CritiquePresse;
import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.repository.FicheFilmRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AllocineServiceTest {

	@Autowired
	private FicheFilmRepository ficheFilmRepository;

	@Test
	public void testFindByTitle() {
		FicheFilm ficheFilm = new FicheFilm();
		ficheFilm.setTitle("title");
		ficheFilm.setAllocineFilmId(1);
		ficheFilm.setPageNumber(1);
		ficheFilm.setUrl("url");
		CritiquePresse cp = new CritiquePresse();
		cp.setAuthor("author1");
		cp.setBody("body1");
		cp.setNewsSource("source1");
		cp.setRating(4d);
		ficheFilm.addCritiquePresse(cp);
		FicheFilm ficheFilmSaved = ficheFilmRepository.save(ficheFilm);
		assertNotNull(ficheFilmSaved);
		FicheFilm ficheFilmRetrieved = ficheFilmRepository.findByTitle("title");
		assertNotNull(ficheFilmRetrieved);
		System.out.println(ficheFilmRetrieved);
	}
}
