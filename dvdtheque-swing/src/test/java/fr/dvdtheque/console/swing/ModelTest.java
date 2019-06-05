package fr.dvdtheque.console.swing;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.dvdtheque.console.swing.ModelTest.TestConfig;
import fr.fredos.dvdtheque.swing.model.FilmTableModel;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class,
		TestConfig.class,
		fr.fredos.dvdtheque.swing.service.FilmRestService.class})
@Ignore
public class ModelTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(ModelTest.class);
	@Autowired
	protected FilmTableModel filmTableModel;

	@Configuration
	protected static class TestConfig{
		@Bean
	    public FilmTableModel filmTableModel() {
	        return new FilmTableModel();
	    }
	}
	@Test
	public void filmTableModel() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		filmTableModel.buildFilmList();
		assertTrue(filmTableModel.getRowCount()>0);
	}
}
