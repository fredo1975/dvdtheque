package fr.fredos.dvdtheque.batch.film.processor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

import javax.jms.Topic;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.batch.model.DvdBuilder;
import fr.fredos.dvdtheque.batch.model.FicheFilm;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceImpl;

public class FilmProcessor implements ItemProcessor<FilmCsvImportFormat,Film> {
	protected Logger logger = LoggerFactory.getLogger(FilmProcessor.class);
	private static String ALLOCINE_SERVICE_URL ="allocine.service.url";
	private static String DVDTHEQUE_SERVICE_URL ="dvdtheque.service.url";
	@Autowired
    private TmdbServiceImpl tmdbServiceClient;
	@Autowired
    Environment environment;
	@Autowired
    private JmsTemplate jmsTemplate;
	@Autowired
    private Topic topic;
	@Autowired
    private RestTemplate restTemplate;
	public FilmProcessor() {
    }
	private static String RIPPEDFLAGTASKLET_FROM_FILE="rippedFlagTasklet.from.file";
	@Override
	public Film process(FilmCsvImportFormat item) throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();
		Film filmTemp = new Film ();
		filmTemp.setTmdbId(item.getTmdbId());
		jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_PROCESSOR_INIT, filmTemp,0l,JmsStatus.FILM_PROCESSOR_INIT.statusValue()));
		
		Film filmToSave = null;
		Results results = tmdbServiceClient.retrieveTmdbSearchResultsById(item.getTmdbId());
		if(results != null) {
			filmToSave = tmdbServiceClient.transformTmdbFilmToDvdThequeFilm(null, results, new HashSet<>(), true);
		}
		
		if(filmToSave != null) {
			filmToSave.setOrigine(FilmOrigine.valueOf(item.getOrigine()));
			if(item.getOrigine().equalsIgnoreCase(FilmOrigine.DVD.name()) || item.getOrigine().equalsIgnoreCase(FilmOrigine.EN_SALLE.name())) {
				DvdBuilder dvdBuilder = new DvdBuilder();
				dvdBuilder.setFilmFormat(item.getFilmFormat());
				dvdBuilder.setDateSortieDvd(item.getDateSortieDvd());
				dvdBuilder.setFilmToSave(filmToSave);
				dvdBuilder.setZonedvd(item.getZonedvd());
				HttpEntity<DvdBuilder> request = new HttpEntity<>(dvdBuilder);
				ResponseEntity<Dvd> dvdResponse = restTemplate.exchange(environment.getRequiredProperty(DVDTHEQUE_SERVICE_URL), HttpMethod.POST, request, Dvd.class);
				filmToSave.setDvd(dvdResponse.getBody());
				boolean loadFromFile = Boolean.valueOf(environment.getRequiredProperty(RIPPEDFLAGTASKLET_FROM_FILE));
				if(!loadFromFile) {
					filmToSave.getDvd().setRipped(false);
				}else {
					if(StringUtils.isEmpty(item.getRipped())) {
						filmToSave.getDvd().setRipped(false);
					}else {
						filmToSave.getDvd().setRipped(item.getRipped().equalsIgnoreCase("oui")?true:false);
						if(item.getRipped().equalsIgnoreCase("oui") && StringUtils.isNotEmpty(item.getRipDate())) {
							DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							filmToSave.getDvd().setDateRip(sdf.parse(item.getRipDate()));
						}
					}
				}
			}
			if(StringUtils.isEmpty(item.getVu())) {
				filmToSave.setVu(false);
			}else {
				filmToSave.setVu(item.getVu().equalsIgnoreCase("oui")?true:false);
			}
			if(StringUtils.isNotEmpty(item.getDateInsertion())) {
				DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				filmToSave.setDateInsertion(sdf.parse(item.getDateInsertion()));
			}else {
				Calendar cal = Calendar.getInstance(Locale.FRANCE);
				cal.set(Calendar.YEAR, 2018);
				cal.set(Calendar.MONTH, 7);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				filmToSave.setDateInsertion(cal.getTime());
			}
			filmToSave.setId(null);
			
			FicheFilm ficheFilm = restTemplate.getForObject(environment.getRequiredProperty(ALLOCINE_SERVICE_URL)+"?title="+item.getTitre(), FicheFilm.class);
			//filmToSave.setCritiquesPresse(ficheFilm.getCritiquesPresse());
			//allocineServiceClient.addCritiquesPresseToFilm(filmToSave);
			
			logger.debug(filmToSave.toString());
			watch.stop();
			jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_PROCESSOR_COMPLETED, filmToSave,watch.getTime(),JmsStatus.FILM_PROCESSOR_COMPLETED.statusValue()));
			logger.debug("Film "+filmToSave.getTitre()+" processing Time Elapsed: " + watch.getTime());
			return filmToSave;
		}
		return null;
	}
}
