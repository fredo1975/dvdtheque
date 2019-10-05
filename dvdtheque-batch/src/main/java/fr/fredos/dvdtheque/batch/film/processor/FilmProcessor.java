package fr.fredos.dvdtheque.batch.film.processor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import javax.jms.Topic;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

public class FilmProcessor implements ItemProcessor<FilmCsvImportFormat,Film> {
	protected Logger logger = LoggerFactory.getLogger(FilmProcessor.class);
	@Autowired
    private TmdbServiceClient tmdbServiceClient;
	@Autowired
	protected IFilmService filmService;
	@Autowired
    Environment environment;
	@Autowired
    private JmsTemplate jmsTemplate;
	@Autowired
    private Topic topic;
	private static String RIPPEDFLAGTASKLET_FROM_FILE="rippedFlagTasklet.from.file";
	@Override
	public Film process(FilmCsvImportFormat item) throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();
		Film filmTemp = new Film ();
		filmTemp.setTmdbId(item.getTmdbId());
		jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_PROCESSOR_INIT, filmTemp,0l,JmsStatus.FILM_PROCESSOR_INIT.statusValue()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Film filmToSave = null;
		Results results = tmdbServiceClient.retrieveTmdbSearchResultsById(item.getTmdbId());
		if(results != null) {
			filmToSave = tmdbServiceClient.transformTmdbFilmToDvdThequeFilm(null, results, new HashSet<>(), true);
		}
		if(filmToSave != null) {
			Dvd dvd = filmService.buildDvd(filmToSave.getAnnee(), item.getZonedvd(), null, null, DvdFormat.valueOf(item.getDvdFormat()));
			filmToSave.setDvd(dvd);
			//filmToSave.setTitreFromExcelFile(StringUtils.upperCase(item.getTitre()));
			boolean loadFromFile = Boolean.valueOf(environment.getRequiredProperty(RIPPEDFLAGTASKLET_FROM_FILE));
			if(!loadFromFile) {
				filmToSave.setRipped(false);
			}else {
				if(StringUtils.isEmpty(item.getRipped())) {
					filmToSave.setRipped(false);
				}else {
					filmToSave.setRipped(item.getRipped().equalsIgnoreCase("oui")?true:false);
					if(item.getRipped().equalsIgnoreCase("oui") && StringUtils.isNotEmpty(item.getRipDate())) {
						DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						filmToSave.getDvd().setDateRip(sdf.parse(item.getRipDate()));
					}
				}
			}
			if(StringUtils.isEmpty(item.getVu())) {
				filmToSave.setVu(false);
			}else {
				filmToSave.setVu(item.getVu().equalsIgnoreCase("oui")?true:false);
			}
			filmToSave.setId(null);
			logger.debug(filmToSave.toString());
			watch.stop();
			jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_PROCESSOR_COMPLETED, filmToSave,watch.getTime(),JmsStatus.FILM_PROCESSOR_COMPLETED.statusValue()));
			logger.debug("Film "+filmToSave.getTitre()+" processing Time Elapsed: " + watch.getTime());
			return filmToSave;
		}
		return null;
	}
}
