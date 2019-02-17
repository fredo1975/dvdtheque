package fr.fredos.dvdtheque.dvdtheque.tmdb.service.test;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;
import fr.fredos.dvdtheque.service.dto.FilmUtils;
import fr.fredos.dvdtheque.tmdb.model.ImagesResults;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class})
public class TmdbServiceClientTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(TmdbServiceClientTest.class);
	@Autowired
    private TmdbServiceClient client;
	private String titre= "broadway";
    @Autowired
	protected FilmService filmService;
    @Autowired
	protected PersonneService personneService;
    private Film createNewFilm() {
		Integer idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_REALISATEUR_ID_SQL, Integer.class);
		if(idRealisateur==null) {
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT1_NOM,FilmUtils.ACT1_PRENOM));
			idRealisateur = this.jdbcTemplate.queryForObject(FilmUtils.MAX_PERSONNE_ID_SQL, Integer.class);
		}
		Integer idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_ACTEUR_ID_SQL, Integer.class);
		if(idActeur1==null) {
			personneService.savePersonne(FilmUtils.buildPersonne(FilmUtils.ACT2_NOM,FilmUtils.ACT2_PRENOM));
			idActeur1 = this.jdbcTemplate.queryForObject(FilmUtils.MAX_PERSONNE_ID_SQL, Integer.class);
		}
		filmService.saveNewFilm(FilmUtils.buildFilm(titre,2015,idRealisateur,idActeur1,null,null));
		Film film = filmService.findFilmByTitre(titre);
		assertNotNull(film);
		return film;
	}
	@Test
    public void retrieveTmdbSearchResultsTest() {
		Film film = createNewFilm();
		assertNotNull(film);
		assertNotNull(film.getTitre());
		SearchResults searchResults = client.retrieveTmdbSearchResults(film.getTitre());
		assertNotNull(searchResults);
		assertNotNull(searchResults.getResults());
		if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
			Results res = searchResults.getResults().get(0);
			assertNotNull(res);
			logger.info("tmdb id"+res.getId().toString());
		}
		
    }
	@Test
    public void retrieveTmdbPosterPathTest() {
		Film film = createNewFilm();
		assertNotNull(film);
		assertNotNull(film.getTitre());
		SearchResults searchResults = client.retrieveTmdbSearchResults(film.getTitre());
		assertNotNull(searchResults);
		assertNotNull(searchResults.getResults());
		if(CollectionUtils.isNotEmpty(searchResults.getResults())) {
			Results res = searchResults.getResults().get(0);
			assertNotNull(res);
			logger.info("tmdb id"+res.getId().toString());
			ImagesResults imagesResults = client.retrieveTmdbImagesResults(res.getId());
			assertNotNull(imagesResults);
			String posterPath = client.retrieveTmdbFrPosterPathUrl(imagesResults);
			logger.info("posterPath="+posterPath);
		}
    }
}
