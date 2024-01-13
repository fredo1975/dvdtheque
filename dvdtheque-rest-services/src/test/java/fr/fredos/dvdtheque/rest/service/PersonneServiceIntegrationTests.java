package fr.fredos.dvdtheque.rest.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.rest.DvdthequeRestApplication;
import fr.fredos.dvdtheque.rest.config.HazelcastConfigurationTest;
import fr.fredos.dvdtheque.rest.config.TestWebSocketConfig;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import fr.fredos.dvdtheque.rest.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.rest.service.model.PersonneDto;
@ActiveProfiles("test")
@SpringBootTest(classes = {TestWebSocketConfig.class,
		HazelcastConfigurationTest.class,
		DvdthequeRestApplication.class})
public class PersonneServiceIntegrationTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceIntegrationTests.class);
	@Autowired
	protected IPersonneService 	personneService;
	@Autowired
	protected IFilmService 		filmService;
	@MockBean
	private JwtDecoder 			jwtDecoder;
	@BeforeEach
	public void cleanAllCaches() {
		personneService.cleanAllCaches();
	}
	
	@Test
	public void getPersonneVersusLoadPersonne() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		assertNotNull(filmId);
		Personne personneByLoad = personneService.loadPersonne(film.getRealisateur().iterator().next().getId());
		assertNotNull(personneByLoad);
		logger.debug("personneByLoad=" + personneByLoad.toString());
	}
	@Test
	public void findPersonne() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		Personne personne = personneService.findByPersonneId(film.getRealisateur().iterator().next().getId());
		assertNotNull(personne);
	}
	
	@Test
	public void findRealisateurByFilm() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		film = filmService.findFilm(film.getId());
		assertNotNull(film);
		assertNotNull(film.getTitre());
		Personne real = personneService.findByPersonneId(film.getRealisateur().iterator().next().getId());
		assertNotNull(real);
	}
	@Test
	public void findAllPersonnes() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		List<Personne> personneList = personneService.findAllPersonne();
		assertNotNull(personneList);
		assertTrue(CollectionUtils.isNotEmpty(personneList));
		assertTrue("personneList.size() should be 4 but is "+personneList.size(),personneList.size()==4);
		for (Personne personne : personneList) {
			Personne p = personneService.findByPersonneId(personne.getId());
			assertNotNull(p);
		}
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		List<Personne> personne2List = personneService.findAllPersonne();
		assertNotNull(personne2List);
		assertTrue(CollectionUtils.isNotEmpty(personne2List));
		assertTrue(personne2List.size()==4);
		for (Personne personne : personne2List) {
			Personne p = personneService.findByPersonneId(personne.getId());
			assertNotNull(p);
		}
	}
	@Test
	public void findPersonneByFullName() throws Exception {
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
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDateSortieDvd(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		Personne personne = personneService.findPersonneByName(FilmBuilder.ACT1_TMBD_ID_844);
		assertNotNull(personne);
	}
	
	@Test
	public void cleanAllPersonne() throws Exception {
		String methodName = "cleanAllPersonne : ";
		logger.debug(methodName + "start");
		personneService.cleanAllPersonnes();
		logger.debug(methodName + "end");
	}
	@Test
	@Disabled
	public void deletePersonne() throws Exception {
		String methodName = "deletePersonne : ";
		logger.debug(methodName + "start");
		Long maxPersonneId = this.jdbcTemplate.queryForObject("select max(ID) from Personne", Long.class);
		// PersonneDto personneDto = personneService.findByPersonneId(maxPersonneId);
		PersonneDto personneDto = new PersonneDto();
		personneDto.setId(maxPersonneId);
		//personneService.deletePersonne(personneDto);
		logger.debug(methodName + "end");
	}
}
