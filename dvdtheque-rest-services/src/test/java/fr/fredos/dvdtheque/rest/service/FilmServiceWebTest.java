package fr.fredos.dvdtheque.rest.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.rest.controller.WebApplication.class})
@ActiveProfiles("test")
public class FilmServiceWebTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@BeforeEach
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
	}
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
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
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		film = filmService.findFilmWithAllObjectGraph(film.getId());
		assertNotNull(film);
		assertNotNull(film.getTitre());
		
		assertNotNull(film.getActeurs());
		for(Personne acteur : film.getActeurs()){
			logger.info(" acteur="+acteur.toString());
		}
	}
	
	@Test
	public void findAllFilms() throws Exception{
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
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(new Integer(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null);
		List<Film> filmList = filmService.findAllFilms(null);
		assertNotNull(filmList);
		logger.debug("filmList ="+filmList.toString());
	}
}
