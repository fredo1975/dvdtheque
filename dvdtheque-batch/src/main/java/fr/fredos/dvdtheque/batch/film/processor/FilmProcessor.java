package fr.fredos.dvdtheque.batch.film.processor;

import java.util.HashSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

public class FilmProcessor implements ItemProcessor<FilmCsvImportFormat,Film> {
	protected Logger logger = LoggerFactory.getLogger(FilmProcessor.class);
	@Autowired
    private TmdbServiceClient tmdbServiceClient;
	@Autowired
	protected IFilmService filmService;
	@Autowired
    Environment environment;
	private static String RIPPEDFLAGTASKLET_FROM_FILE="rippedFlagTasklet.from.file";
	@Override
	public Film process(FilmCsvImportFormat item) throws Exception {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Film filmToSave = null;
		SearchResults searchResults = tmdbServiceClient.retrieveTmdbSearchResults(item.getTitre());
		if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
			Results res = tmdbServiceClient.filterSearchResultsByDateRelease(item.getAnnee(), searchResults.getResults());
			if(res != null) {
				filmToSave = tmdbServiceClient.transformTmdbFilmToDvdThequeFilm(null, res, new HashSet<>(), true);
			}
		}
		if(filmToSave != null) {
			Dvd dvd = filmService.buildDvd(filmToSave.getAnnee(), item.getZonedvd(), null);
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
				}
			}
			filmToSave.setId(null);
			logger.debug(filmToSave.toString());
			return filmToSave;
		}
		return null;
	}
}
