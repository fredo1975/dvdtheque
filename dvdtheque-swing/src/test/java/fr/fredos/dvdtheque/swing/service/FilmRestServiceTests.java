package fr.fredos.dvdtheque.swing.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.tmdb.service.FilmRestService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class})
public class FilmRestServiceTests extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(FilmRestServiceTests.class);
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
	protected FilmRestService filmRestService;
	
	@Test
	public void findAllFilmsRestService() {
		List<Film> filmList = filmService.findAllFilms();
		assertNotNull(filmList);
	}
}
