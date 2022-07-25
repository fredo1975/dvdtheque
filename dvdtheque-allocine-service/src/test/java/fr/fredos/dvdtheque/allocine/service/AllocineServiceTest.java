package fr.fredos.dvdtheque.allocine.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
	public void testRemoveFilm() {
		FicheFilm ficheFilmSaved = saveFilm();
		final String title = ficheFilmSaved.getTitle();
		ficheFilmRepository.delete(ficheFilmSaved);
		List<FicheFilm> ficheFilmRetrieved = ficheFilmRepository.findByTitle(title);
		assertTrue(CollectionUtils.isEmpty(ficheFilmRetrieved));
	}
	private FicheFilm saveFilm() {
		FicheFilm ficheFilm = new FicheFilm("title",1,"url",1);
		CritiquePresse cp = new CritiquePresse();
		cp.setAuthor("author1");
		cp.setBody("body1");
		cp.setNewsSource("source1");
		cp.setRating(4d);
		cp.setFicheFilm(ficheFilm);
		ficheFilm.addCritiquePresse(cp);
		FicheFilm ficheFilmSaved = ficheFilmRepository.save(ficheFilm);
		assertNotNull(ficheFilmSaved);
		return ficheFilmSaved;
	}
	@Test
	public void testFindByTitle() {
		FicheFilm ficheFilmSaved = saveFilm();
		assertNotNull(ficheFilmSaved);
		List<FicheFilm> ficheFilmRetrieved = ficheFilmRepository.findByTitle("title");
		assertNotNull(ficheFilmRetrieved);
		assertNotNull(ficheFilmRetrieved.get(0));
		assertNotNull(ficheFilmRetrieved.get(0).getCreationDate());
		assertNotNull(ficheFilmRetrieved.get(0).getCritiquePresse());
		assertTrue(ficheFilmRetrieved.get(0).getCritiquePresse().iterator().next().getNewsSource().equals("source1"));
		System.out.println(ficheFilmRetrieved);
	}
}
