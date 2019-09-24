package fr.fredos.dvdtheque.batch.film.writer;

import java.util.List;

import javax.jms.Topic;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;

public class DbFilmWriter implements ItemWriter<Film> {
	protected Logger logger = LoggerFactory.getLogger(DbFilmWriter.class);
	@Autowired
	protected IFilmService filmService;
	@Autowired
    private JmsTemplate jmsTemplate;
	@Autowired
    private Topic topic;
	@Override
	public void write(List<? extends Film> items) throws Exception {
		for(Film film : items){
			if(film != null) {
				StopWatch watch = new StopWatch();
				watch.start();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.DB_FILM_WRITER_INIT, film));
				filmService.saveNewFilm(film);
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.DB_FILM_WRITER_COMPLETED, film));
				watch.stop();
				logger.debug("Film "+film.getTitre()+" insertion Time Elapsed: " + watch.getTime());
			}
		}
	}
}
