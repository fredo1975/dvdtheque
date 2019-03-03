package fr.fredos.dvdtheque.dvdtheque.tmdb.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

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
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.tmdb.model.Credits;
import fr.fredos.dvdtheque.tmdb.model.Crew;
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
	public static final String TITRE_FILM = "broadway";
	public static final String TITRE_FILM_UPDATED = "Lorem Ipsum updated";
	public static final Integer ANNEE = 2015;
	public static final String REAL_NOM = "toto titi";
	public static final String REAL_NOM1 = "Dan VanHarp";
	public static final String ACT1_NOM = "tata tutu";
	public static final String ACT2_NOM = "toitoi tuitui";
	public static final String ACT3_NOM = "tuotuo tmitmi";
	public static final String ACT4_NOM = "Graham Collins";
	@Autowired
    private TmdbServiceClient client;
	private String titreTmdb= "2001";
	private Long tmdbId= new Long(4780);
    @Autowired
	protected IFilmService filmService;
    @Autowired
	protected IPersonneService personneService;
    
    private void assertFilmIsNotNull(Film film) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
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
    public void retrieveTmdbResultsTest() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		Results res = getResultsByFilmTitre(film);
		assertNotNull(res);
		logger.info("tmdb id = "+res.getId().toString());
    }
	@Test
    public void retrieveTmdbResultsByTmdbIdTest() {
		Results res = client.retrieveTmdbSearchResultsById(tmdbId);
		assertNotNull(res);
		logger.info("res = "+res.toString());
    }
	@Test
    public void replaceFilmTest() throws ParseException {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		film = client.replaceFilm(tmdbId, film);
		assertFilmIsNotNull(film);
		logger.info("film = "+film.toString());
    }
	@Test
    public void retrieveTmdbFilmListToDvdthequeFilmListTest() throws ParseException {
		Set<Film> filmSet = client.retrieveTmdbFilmListToDvdthequeFilmList(titreTmdb);
		assertNotNull(filmSet);
		assertTrue(CollectionUtils.isNotEmpty(filmSet));
		for(Film film : filmSet) {
			logger.info("film = "+film.toString());
		}
    }
	@Test
    public void retrieveTmdbPosterPathTest() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		assertNotNull(film);
		assertNotNull(film.getTitre());
		Results res = getResultsByFilmTitre(film);
		assertNotNull(res);
		logger.info("tmdb id = "+res.getId().toString());
		
		ImagesResults imagesResults = client.retrieveTmdbImagesResults(res.getId());
		assertNotNull(imagesResults);
		String posterPath = client.retrieveTmdbFrPosterPathUrl(imagesResults);
		logger.info("posterPath="+posterPath);
    }
	@Test
    public void retrieveTmdbCreditsTest() {
		Film film = filmService.createOrRetrieveFilm(TITRE_FILM, ANNEE,REAL_NOM,ACT1_NOM,ACT2_NOM,ACT3_NOM);
		assertFilmIsNotNull(film);
		
		assertNotNull(film);
		assertNotNull(film.getTitre());
		Results res = getResultsByFilmTitre(film);
		assertNotNull(res);
		
		Credits credits = client.retrieveTmdbCredits(res.getId());
		assertNotNull(credits.getCast());
		assertNotNull(credits.getCrew());
		logger.info("credits.getCast()="+credits.getCast());
		logger.info("credits.getCrew()="+credits.getCrew());
		logger.info("directors="+client.retrieveTmdbDirectors(credits));
		assertTrue(CollectionUtils.isNotEmpty(client.retrieveTmdbDirectors(credits)));
    }
}
