package fr.fredos.dvdtheque.batch.film.writer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.jms.Topic;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;

public class DbFilmWriter implements ItemWriter<Film> {
	protected Logger logger = LoggerFactory.getLogger(DbFilmWriter.class);
	public static String DVDTHEQUE_SERVICE_URL="dvdtheque-service.url";
	public static String DVDTHEQUE_SERVICE_SAVE_FILM="dvdtheque-service.saveFilm";
	
	@Autowired
    private JmsTemplate 											jmsTemplate;
	@Autowired
    Environment 													environment;
    @Autowired
    private RestTemplate 											restTemplate;
	@Autowired
    private Topic topic;
	@Autowired
	AuthorizedClientServiceOAuth2AuthorizedClientManager 			authorizedClientServiceAndManager;
	@Override
	public void write(List<? extends Film> items) throws Exception {
		logger.info("##### write");
		for(Film film : items){
			logger.info("##### write film="+film.toString());
			if(film != null) {
				StopWatch watch = new StopWatch();
				watch.start();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.DB_FILM_WRITER_INIT, film,0l,JmsStatus.DB_FILM_WRITER_INIT.statusValue()));
				
				OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
						.principal("batch")
						.build();

				OAuth2AuthorizedClient authorizedClient = this.authorizedClientServiceAndManager.authorize(authorizeRequest);

				OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.add("Authorization", "Bearer " + accessToken.getTokenValue());
		        HttpEntity<?> request = new HttpEntity<>(film,headers);
		        ResponseEntity<Film> resultsResponse = restTemplate.exchange(environment.getRequiredProperty(DVDTHEQUE_SERVICE_URL)
						+environment.getRequiredProperty(DVDTHEQUE_SERVICE_SAVE_FILM), 
						HttpMethod.POST, 
						request, 
						Film.class);
				watch.stop();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.DB_FILM_WRITER_COMPLETED, film,watch.getTime(),JmsStatus.DB_FILM_WRITER_COMPLETED.statusValue()));
				logger.debug("Film "+film.getTitre()+" insertion Time Elapsed: " + watch.getTime());
			}
		}
	}
}
