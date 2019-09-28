package fr.fredos.dvdtheque.dvdtheque.tmdb.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.tmdb.model.Credits;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.model.SearchResults;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class})
public class TmdbServiceClientTest extends AbstractTransactionalJUnit4SpringContextTests{
	protected Logger logger = LoggerFactory.getLogger(TmdbServiceClientTest.class);
	public static final String TITRE_FILM = "broadway";
	public static final String TITRE_FILM_UPDATED = "Lorem Ipsum updated";
	public static final Integer ANNEE = 2015;
	public static final String REAL_NOM = "toto titi";
	public static final String REAL_NOM1 = "Dan VanHarp";
	public static final String ACT1_NOM = "tata tutu";
	public static final String ACT2_NOM = "toitoi tuitui";
	public static final String ACT3_NOM = "tuotuo tmitmi";
	public static final String ACT4_NOM = "Graham Collins";
	public static final int RIP_DATE = -10;
	@Autowired
    private TmdbServiceClient client;
	private String titreTmdb= "2001";
	private Long tmdbId;
	private Long tmdbIdToSave;
    @Autowired
	protected IFilmService filmService;
    @Autowired
	protected IPersonneService personneService;
    
    @Before()
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
		/*findTmdbFilmToInsert();
		findTmdbFilmToTestSave();*/
	}
    private void findTmdbFilmToInsert() throws Exception{
		boolean found = false;
		while(!found) {
			this.tmdbId = ThreadLocalRandom.current().nextLong(200, 40000);
			if(!filmService.checkIfTmdbFilmExists(this.tmdbId)) {
				found = true;
			}
		}
		Film filmSaved = client.saveTmbdFilm(tmdbId);
		if(filmSaved == null) {
			findTmdbFilmToInsert();
		}
	}
    private void findTmdbFilmToTestSave() {
		boolean found = false;
		while(!found) {
			this.tmdbIdToSave = ThreadLocalRandom.current().nextLong(200, 3000);
			Results res = client.retrieveTmdbSearchResultsById(this.tmdbIdToSave);
			try {
				assertResultsIsNotNull(res);
				found = true;
			}catch(AssertionError err) {
				logger.info("findTmdbFilmToTestSave retrying another tmdbIdToSave for tmdbIdToSave="+tmdbIdToSave);
			}
		}
	}
    private void assertResultsIsNotNull(Results res) {
		assertNotNull(res);
		assertNotNull(res.getId());
		assertNotNull(res.getOriginal_title());
		assertNotNull(res.getPoster_path());
		assertNotNull(res.getTitle());
		assertNotNull(res.getRelease_date());
		assertNotNull(res.getOverview());
		assertNotNull(res.getRuntime());
	}
    private Date createRipDate() {
		Calendar cal = Calendar.getInstance();
		return DateUtils.addDays(cal.getTime(), RIP_DATE);
	}
    private void assertFilmIsNotNull(Film film,boolean dateRipNull) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		if(!dateRipNull) {
			assertEquals(filmService.clearDate(createRipDate()),film.getDvd().getDateRip());
		}
		assertNotNull(film.getOverview());
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()>=3);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
	}
    private Results getResultsByFilmTitre(Film film) {
    	SearchResults searchResults = client.retrieveTmdbSearchResults(film.getTitre());
		assertNotNull(searchResults);
		assertNotNull(searchResults.getResults());
		return client.filterSearchResultsByDateRelease(film.getAnnee(), searchResults.getResults());
    }
	@Test
	@Ignore
    public void retrieveTmdbResultsTest() {
		Results res = client.retrieveTmdbSearchResultsById(this.tmdbId);
		assertResultsIsNotNull(res);
		logger.info("tmdb id = "+res.getId().toString());
    }
	@Test
	@Ignore
    public void retrieveTmdbResultsWithResourceNotFoundTest() {
		Long tmdbId = Long.valueOf(413);
		Results results = client.retrieveTmdbSearchResultsById(tmdbId);
		assertNull(results);
		//logger.info("tmdb id = "+res.getId().toString());
    }
	@Test
    public void retrieveTmdbResultsByTmdbIdTest() {
		Results res = client.retrieveTmdbSearchResultsById(Long.valueOf(55));
		assertResultsIsNotNull(res);
		logger.info("res = "+res.toString());
    }
	@Test
    public void replaceFilmTest() throws Exception {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM, null, DvdFormat.DVD);
		assertFilmIsNotNull(film,true);
		Boolean exists = filmService.checkIfTmdbFilmExists(612152l);
		if(!exists) {
			film = client.replaceFilm(612152l, film);
			assertFilmIsNotNull(film,true);
			logger.info("film = "+film.toString()+" replaced");
		}else {
			logger.info("film = "+film.toString()+" already existing");
		}
    }
	@Test
    public void savetmdbFilmTest() throws Exception {
		Film film = client.saveTmbdFilm(13457l);
		assertFilmIsNotNull(film,true);
		//assertEquals(new Integer(98), film.getRuntime());
		logger.info("film = "+film.toString());
    }
	@Test
	@Ignore
    public void retrieveTmdbFilmListToDvdthequeFilmListTest() throws ParseException {
		Set<Film> filmSet = client.retrieveTmdbFilmListToDvdthequeFilmList(titreTmdb);
		assertNotNull(filmSet);
		assertTrue(CollectionUtils.isNotEmpty(filmSet));
		for(Film film : filmSet) {
			logger.info("film = "+film.toString());
		}
    }
	/*
	@Test
    public void retrieveTmdbPosterPathTest() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		assertNotNull(film);
		assertNotNull(film.getTitre());
		Results res = getResultsByFilmTitre(film);
		assertResultsIsNotNull(res);
		logger.info("tmdb id = "+res.getId().toString());
		
		ImagesResults imagesResults = client.retrieveTmdbImagesResults(res.getId());
		assertNotNull(imagesResults);
		String posterPath = client.retrieveTmdbFrPosterPathUrl(imagesResults);
		logger.info("posterPath="+posterPath);
    }*/
	@Test
    public void retrieveTmdbCreditsTest() {
		Results res = client.retrieveTmdbSearchResultsById(335070l);
		assertResultsIsNotNull(res);
		
		Credits credits = client.retrieveTmdbCredits(res.getId());
		assertNotNull(credits.getCast());
		assertNotNull(credits.getCrew());
		logger.info("credits.getCast()="+credits.getCast());
		logger.info("credits.getCrew()="+credits.getCrew());
		logger.info("directors="+client.retrieveTmdbDirectors(credits));
		assertTrue(CollectionUtils.isNotEmpty(client.retrieveTmdbDirectors(credits)));
    }
}
