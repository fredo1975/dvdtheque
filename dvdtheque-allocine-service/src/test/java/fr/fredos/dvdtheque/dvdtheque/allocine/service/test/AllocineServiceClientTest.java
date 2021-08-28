package fr.fredos.dvdtheque.dvdtheque.allocine.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.allocine.model.SearchResults;
import fr.fredos.dvdtheque.allocine.service.AllocineServiceClient;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.service.IFilmService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.allocine.service.AllocineServiceApplication.class})
@ActiveProfiles("local")
public class AllocineServiceClientTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceClientTest.class);
	@Autowired
    private AllocineServiceClient client;
    @Autowired
	protected IFilmService filmService;
    
    @Before()
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
	}
    
    private void assertSearchMovieFeedResultsIsNotNull(SearchResults res) {
		assertNotNull(res);
		assertNotNull(res.getFeed());
		assertNotNull(res.getFeed().getMovie());
		assertTrue(CollectionUtils.isNotEmpty(res.getFeed().getMovie()));
		assertNotNull(res.getFeed().getMovie().get(0));
		assertNotNull(res.getFeed().getMovie().get(0).getCode());
	}
    
    private void assertSearchReviewFeedResultsIsNotNull(SearchResults res) {
		assertNotNull(res);
		assertNotNull(res.getFeed());
		assertNotNull(res.getFeed().getReview());
		assertTrue(CollectionUtils.isNotEmpty(res.getFeed().getReview()));
		assertNotNull(res.getFeed().getReview().get(0));
		assertNotNull(res.getFeed().getReview().get(0).getNewsSource());
		assertNotNull(res.getFeed().getReview().get(0).getNewsSource().getCode());
	}
    
    @Test
    @Disabled
    public void retrieveAllocineMovieFeedByTitleTest() {
    	SearchResults searchResults = client.retrieveAllocineMovieFeedByTitle(FilmBuilder.TITRE_FILM_TMBD_ID_844);
    	assertSearchMovieFeedResultsIsNotNull(searchResults);
		logger.info("allocine feed = "+searchResults.getFeed());
    }
    
    @Test
    @Disabled
    public void retrieveAllocineReviewFeedByCodeTest() {
    	SearchResults searchResults = client.retrieveAllocineMovieFeedByTitle(FilmBuilder.TITRE_FILM_TMBD_ID_844);
    	assertSearchMovieFeedResultsIsNotNull(searchResults);
    	int code = searchResults.getFeed().getMovie().get(0).getCode();
    	SearchResults searchReviewFeedResults = client.retrieveAllocineReviewFeedByCode(code,1);
    	assertSearchReviewFeedResultsIsNotNull(searchReviewFeedResults);
    	logger.info("allocine feed = "+searchResults.getFeed());
    }
    
    @Test
    @Disabled
    public void retrieveReviewListToCritiquesPresseListTest() throws ParseException {
    	Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_10315)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_10315)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		client.addCritiquesPresseToFilm(film);
		assertTrue(CollectionUtils.isNotEmpty(film.getCritiquesPresse()));
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film dbFilm = filmService.findFilm(filmId);
		assertNotNull(dbFilm);
		assertNotNull(dbFilm.getCritiquesPresse());
		assertTrue("",dbFilm.getCritiquesPresse().size() == film.getCritiquesPresse().size());
		assertTrue("should be 28 critiques presse",dbFilm.getCritiquesPresse().size() == 28);
    }
}
