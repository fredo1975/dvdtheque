package fr.fredos.dvdtheque.batch.film.processor;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.service.dto.ActeurDto;
import fr.fredos.dvdtheque.service.dto.DvdDto;
import fr.fredos.dvdtheque.service.dto.FilmDto;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
import fr.fredos.dvdtheque.service.dto.PersonnesFilm;
import fr.fredos.dvdtheque.service.dto.RealisateurDto;
import fr.fredos.dvdtheque.tmdb.model.Cast;
import fr.fredos.dvdtheque.tmdb.model.Credits;
import fr.fredos.dvdtheque.tmdb.model.ImagesResults;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

public class FilmProcessor implements ItemProcessor<FilmCsvImportFormat,FilmDto> {
	protected Logger logger = LoggerFactory.getLogger(FilmProcessor.class);
	@Autowired
    private TmdbServiceClient tmdbServiceClient;
	@Override
	public FilmDto process(FilmCsvImportFormat item) throws Exception {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FilmDto film=new FilmDto();
		film.setAnnee(item.getAnnee());
		film.setTitre(StringUtils.upperCase(item.getTitre()));
		//filmDto.setTitreO(item.getTitreO());
		DvdDto dvd = new DvdDto();
		dvd.setZone(item.getZonedvd());
		film.setDvd(dvd);
		PersonnesFilm pf = new PersonnesFilm();
		try {
			SearchResults searchResults = tmdbServiceClient.retrieveTmdbSearchResults(film.getTitre());
			if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
				Results res = tmdbServiceClient.filterSearchResultsByDateRelease(film.getAnnee(), searchResults.getResults());
				ImagesResults imagesResults = tmdbServiceClient.retrieveTmdbImagesResults(res.getId());
				if(CollectionUtils.isNotEmpty(imagesResults.getPosters())) {
					String imageUrl = tmdbServiceClient.retrieveTmdbFrPosterPathUrl(imagesResults);
					film.setPosterPath(imageUrl);
				}
				Credits credits = tmdbServiceClient.retrieveTmdbCredits(res.getId());
				if(CollectionUtils.isNotEmpty(credits.getCast())) {
					Set<ActeurDto> acteursDto = new HashSet<>();
					int i=0;
					for(Cast cast : credits.getCast()) {
						PersonneDto personne = new PersonneDto();
						personne.setNom(StringUtils.upperCase(cast.getName()));
						ActeurDto acteurDto = new ActeurDto();
						acteurDto.setPersonne(personne);
						acteursDto.add(acteurDto);
						if(i++==5) {
							break;
						}
					}
					pf.setActeur(acteursDto);
				}
				if(CollectionUtils.isNotEmpty(credits.getCrew())) {
					Set<RealisateurDto> realisateur = new HashSet<>(1);
					RealisateurDto realisateurDto = new RealisateurDto();
					PersonneDto personne = new PersonneDto();
					personne.setNom(StringUtils.upperCase(tmdbServiceClient.retrieveTmdbDirector(credits)));
					realisateurDto.setPersonne(personne);
					realisateur.add(realisateurDto);
					pf.setRealisateur(realisateur.iterator().next());
				}
				film.setTitreO(res.getOriginal_title());
			}
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		film.setPersonnesFilm(pf);
		film.setRipped(false);
		//logger.info(film.toString());
		return film;
	}
}
