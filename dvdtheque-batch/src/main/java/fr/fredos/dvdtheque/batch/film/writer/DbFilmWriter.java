package fr.fredos.dvdtheque.batch.film.writer;

import java.util.List;

import javax.jms.Topic;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;

public class DbFilmWriter implements ItemWriter<Film> {
	protected Logger logger = LoggerFactory.getLogger(DbFilmWriter.class);
	@Autowired
    private JmsTemplate jmsTemplate;
	@Autowired
    Environment environment;
    @Autowired
    private RestTemplate restTemplate;
	@Autowired
    private Topic topic;
	@Override
	public void write(List<? extends Film> items) throws Exception {
		for(Film film : items){
			if(film != null) {
				StopWatch watch = new StopWatch();
				watch.start();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.DB_FILM_WRITER_INIT, film,0l,JmsStatus.DB_FILM_WRITER_INIT.statusValue()));
				//restTemplate.exchange(null, HttpMethod.PUT,)
				watch.stop();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.DB_FILM_WRITER_COMPLETED, film,watch.getTime(),JmsStatus.DB_FILM_WRITER_COMPLETED.statusValue()));
				logger.debug("Film "+film.getTitre()+" insertion Time Elapsed: " + watch.getTime());
			}
		}
	}
}
