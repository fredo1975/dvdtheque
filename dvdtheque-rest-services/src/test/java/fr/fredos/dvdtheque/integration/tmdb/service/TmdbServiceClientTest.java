package fr.fredos.dvdtheque.integration.tmdb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
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

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.integration.config.HazelcastConfiguration;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.tmdb.model.Credits;
import fr.fredos.dvdtheque.tmdb.model.Results;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class,
		fr.fredos.dvdtheque.allocine.service.AllocineServiceApplication.class,
		HazelcastConfiguration.class},
properties = { "eureka.client.enabled:false", "spring.cloud.config.enabled:false" })
@ActiveProfiles("test")
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
    @Autowired
	protected IFilmService filmService;
    @Autowired
	protected IPersonneService personneService;
    
    @Before()
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
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
	
	@Test
    public void retrieveTmdbResultsTest() {
		Results res = client.retrieveTmdbSearchResultsById(FilmBuilder.tmdbId1);
		assertResultsIsNotNull(res);
		logger.info("tmdb id = "+res.getId().toString());
    }
	@Test
	@Disabled
    public void retrieveTmdbResultsWithResourceNotFoundTest() {
		Long tmdbId = Long.valueOf(413);
		Results results = client.retrieveTmdbSearchResultsById(tmdbId);
		assertNull(results);
		//logger.info("tmdb id = "+res.getId().toString());
    }
	@Test
    public void retrieveTmdbResultsByTmdbIdTest() {
		Results res = client.retrieveTmdbSearchResultsById(FilmBuilder.tmdbId1);
		assertResultsIsNotNull(res);
		logger.info("res = "+res.toString());
    }
	@Test
    public void retrieveTmdbFrReleaseDateTest() throws ParseException {
		Date res = client.retrieveTmdbFrReleaseDate(FilmBuilder.tmdbId1);
		assertNotNull(res);
		String relDate = FilmBuilder.TMDBID1_DATE_SORTIE;
		String pattern = "yyyy/MM/dd";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date realDate = sdf.parse(relDate);
		assertEquals("Release date should match",realDate,res);
    }
	@Test
    public void replaceFilmTest() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Boolean exists = filmService.checkIfTmdbFilmExists(FilmBuilder.tmdbId1);
		if(!exists) {
			film = client.replaceFilm(FilmBuilder.tmdbId1, film);
			FilmBuilder.assertFilmIsNotNull(film, true, 0, FilmOrigine.DVD, FilmBuilder.TMDBID1_DATE_SORTIE, null);
			logger.info("film = "+film.toString()+" replaced");
		}else {
			logger.info("film = "+film.toString()+" already existing");
		}
    }
	@Test
    public void savetmdbFilmTest() throws Exception {
		Film film = client.saveTmbdFilm(FilmBuilder.tmdbId1, FilmOrigine.DVD);
		String dateInsertion = FilmBuilder.createDateInsertion(null, null);
		FilmBuilder.assertFilmIsNotNull(film, true, 0, FilmOrigine.DVD, FilmBuilder.TMDBID1_DATE_SORTIE, dateInsertion);
    }
	@Test
    public void retrieveTmdbFilmListToDvdthequeFilmListTest() throws ParseException {
		List<Film> filmSet = client.retrieveTmdbFilmListToDvdthequeFilmList(FilmBuilder.TITRE_FILM_FOR_SEARCH_BY_TITRE);
		assertNotNull(filmSet);
		assertTrue(CollectionUtils.isNotEmpty(filmSet));
		assertTrue(filmSet.size()>=129);
		for(Film film : filmSet) {
			logger.debug("film = "+film.toString());
		}
    }
	
	@Test
    public void retrieveTmdbCreditsTest() {
		Results res = client.retrieveTmdbSearchResultsById(FilmBuilder.tmdbId1);
		assertResultsIsNotNull(res);
		
		Credits credits = client.retrieveTmdbCredits(res.getId());
		assertNotNull(credits.getCast());
		assertNotNull(credits.getCrew());
		/*logger.info("credits.getCast()="+credits.getCast());
		logger.info("credits.getCrew()="+credits.getCrew());
		logger.info("directors="+client.retrieveTmdbDirectors(credits));*/
		assertTrue(CollectionUtils.isNotEmpty(client.retrieveTmdbDirectors(credits)));
    }
}
