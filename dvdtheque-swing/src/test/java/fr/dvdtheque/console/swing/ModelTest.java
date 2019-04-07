package fr.dvdtheque.console.swing;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.swing.model.FilmTableModel;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class,
		fr.fredos.dvdtheque.swing.SwingConfig.class})
public class ModelTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(ModelTest.class);
	@Autowired
	protected FilmTableModel filmTableModel;
	
	@Test
	public void filmTableModel() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		filmTableModel.buildFilmList();
		
	}
}
