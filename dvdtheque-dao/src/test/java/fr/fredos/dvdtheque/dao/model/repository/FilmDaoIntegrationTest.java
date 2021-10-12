package fr.fredos.dvdtheque.dao.model.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.repository.impl.FilmDaoImpl;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(FilmDaoImpl.class)
@DataJpaTest
public class FilmDaoIntegrationTest {
	protected Logger logger = LoggerFactory.getLogger(FilmDaoIntegrationTest.class);
	@Autowired
    private FilmDao filmDao;
	private static final int GENRE_TMDB_ID_1 = 28;
	private static final int GENRE_TMDB_ID_2 = 29;
	private static final String GENRE_NAME_1 = "Action";
	private static final String GENRE_NAME_2 = "Comédie";
	
	private Genre saveGenre(final int tmdbId, final String name) {
		return filmDao.saveGenre(new Genre(tmdbId,name));
	}
	
	@Test
	public void saveGenre() {
		Genre genreRetrieved = saveGenre(GENRE_TMDB_ID_1, GENRE_NAME_1);
		assertNotNull(genreRetrieved);
	}
	
	@Test
	public void findAllFilms() throws ParseException {
		Genre genre1 = saveGenre(GENRE_TMDB_ID_1, GENRE_NAME_1);
		Genre genre2 = saveGenre(GENRE_TMDB_ID_2, GENRE_NAME_2);
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmDao.saveNewFilm(film);
		assertNotNull(filmId);
		List<Film> films = filmDao.findAllFilms();
		assertNotNull(films);
		assertEquals(1, films.size());
		FilmBuilder.assertFilmIsNotNull(films.get(0), false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		logger.info("films.size()="+films.size());
	}
	
	@Test
	public void findGenre() {
		Genre genre1 = saveGenre(GENRE_TMDB_ID_1, GENRE_NAME_1);
		Genre genre2 = saveGenre(GENRE_TMDB_ID_2, GENRE_NAME_2);
		assertNotNull(genre1);
		assertNotNull(genre2);
		final List<Genre> genreList = filmDao.findAllGenres();
		assertNotNull(genreList);
		assertEquals(2, genreList.size());
		Genre genreRetrieved1 = filmDao.findGenre(genreList.get(0).getTmdbId());
		assertNotNull(genreRetrieved1);
		assertEquals(GENRE_TMDB_ID_1, genreRetrieved1.getTmdbId());
		assertEquals(GENRE_NAME_1, genreRetrieved1.getName());
		Genre genreRetrieved2 = filmDao.findGenre(genreList.get(1).getTmdbId());
		assertNotNull(genreRetrieved2);
		assertEquals(GENRE_TMDB_ID_2, genreRetrieved2.getTmdbId());
		assertEquals(GENRE_NAME_2, genreRetrieved2.getName());
	}
	
}
