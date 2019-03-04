package fr.fredos.dvdtheque.batch.film.processor;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClientException;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.tmdb.model.Cast;
import fr.fredos.dvdtheque.tmdb.model.Credits;
import fr.fredos.dvdtheque.tmdb.model.Crew;
import fr.fredos.dvdtheque.tmdb.model.ImagesResults;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

public class FilmProcessor implements ItemProcessor<FilmCsvImportFormat,Film> {
	protected Logger logger = LoggerFactory.getLogger(FilmProcessor.class);
	@Autowired
    private TmdbServiceClient tmdbServiceClient;
	private static String NB_ACTEURS="batch.save.nb.acteurs";
	@Autowired
	protected IPersonneService personneService;
	@Autowired
    Environment environment;
	@Override
	public Film process(FilmCsvImportFormat item) throws Exception {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Film film=new Film();
		film.setAnnee(item.getAnnee());
		film.setTitre(StringUtils.upperCase(item.getTitre()));
		//film.setTitreO(item.getTitreO());
		Dvd dvd = new Dvd();
		dvd.setZone(item.getZonedvd());
		film.setDvd(dvd);
		try {
			SearchResults searchResults = tmdbServiceClient.retrieveTmdbSearchResults(film.getTitre());
			if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
				Results res = tmdbServiceClient.filterSearchResultsByDateRelease(film.getAnnee(), searchResults.getResults());
				film.setTmdbId(res.getId());
				ImagesResults imagesResults = tmdbServiceClient.retrieveTmdbImagesResults(res.getId());
				if(CollectionUtils.isNotEmpty(imagesResults.getPosters())) {
					String imageUrl = tmdbServiceClient.retrieveTmdbFrPosterPathUrl(imagesResults);
					film.setPosterPath(imageUrl);
				}
				Credits credits = tmdbServiceClient.retrieveTmdbCredits(res.getId());
				if(CollectionUtils.isNotEmpty(credits.getCast())) {
					int i=1;
					for(Cast c : credits.getCast()) {
						Personne personne = personneService.createOrRetrievePersonne(StringUtils.upperCase(c.getName()));
						film.getActeurs().add(personne);
						if(i++==Integer.parseInt(environment.getRequiredProperty(NB_ACTEURS))) {
							break;
						}
					}
				}
				if(CollectionUtils.isNotEmpty(credits.getCrew())) {
					List<Crew> crew = tmdbServiceClient.retrieveTmdbDirectors(credits);
					for(Crew c : crew) {
						Personne realisateur = personneService.createOrRetrievePersonne(StringUtils.upperCase(c.getName()));
						film.getRealisateurs().add(realisateur);
					}
				}
				film.setTitreO(StringUtils.upperCase(res.getOriginal_title()));
			}
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		film.setRipped(false);
		logger.debug(film.toString());
		return film;
	}
}
