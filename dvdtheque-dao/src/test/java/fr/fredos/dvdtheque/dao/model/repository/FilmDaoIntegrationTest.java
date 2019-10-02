package fr.fredos.dvdtheque.dao.model.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.Application;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class FilmDaoIntegrationTest {
	protected Logger logger = LoggerFactory.getLogger(FilmDaoIntegrationTest.class);
	@Autowired
    private FilmDao filmDao;
	
	@Test
	public void findAllFilms() {
		List<Film> films = filmDao.findAllFilms();
		assertNotNull(films);
		logger.info("films.size()="+films.size());
	}
	@Test(expected=EmptyResultDataAccessException.class)
	public void findGenre() {
		Genre genre = filmDao.findGenre(28);
		assertNull(genre);
	}
	
}
