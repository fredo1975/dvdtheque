package fr.fredos.dvdtheque.web.controller;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,fr.fredos.dvdtheque.service.ServiceApplication.class,fr.fredos.dvdtheque.web.controller.WebApplication.class})
public class FilmServiceTest {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceTest.class);
	@Autowired
	protected FilmService filmService;
	
	@Test
	public void findAllFilm() throws Exception {
		String methodName = "findAllFilm : ";
		logger.debug(methodName + "start");
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = filmService.findAllFilms();
		
		assertNotNull(films);
		watch.stop();
		logger.debug(watch.prettyPrint());
		logger.debug(methodName + "end");
	}
}
