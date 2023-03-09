package fr.fredos.dvdtheque.rest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.integration.config.ContextConfiguration;
import fr.fredos.dvdtheque.integration.config.HazelcastConfiguration;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import fr.fredos.dvdtheque.rest.dao.model.utils.FilmBuilder;
import fr.fredos.dvdtheque.rest.dao.repository.DvdDao;
import fr.fredos.dvdtheque.rest.dao.repository.FilmDao;
import fr.fredos.dvdtheque.rest.exception.FilmNotFoundException;
import fr.fredos.dvdtheque.rest.model.ExcelFilmHandler;

@SpringBootTest(classes = {HazelcastConfiguration.class, ContextConfiguration.class})
@ActiveProfiles("test")
public class FilmServiceIntegrationTests extends AbstractTransactionalJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceIntegrationTests.class);
	
	@Autowired
	private FilmDao 			filmDao;
	@Autowired
	private DvdDao 				dvdDao;
	@Autowired
	private IFilmService 		filmService;
	@Autowired
	private IPersonneService 	personneService;
	@Autowired
	private ExcelFilmHandler 	excelFilmHandler;
	@MockBean
	private JwtDecoder 			jwtDecoder;
	@BeforeEach
	public void cleanAllCaches() {
		filmService.cleanAllCaches();
	}
	@Test
	public void search() throws ParseException{
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		var query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_844+":AND";
		var l = filmService.search(query, 1, 1, "-titre");
		assertNotNull(l);
		var it = l.iterator();
		assertNotNull(it);
		var f = it.next();
		assertNotNull(f);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(f.getTitre());
		query = "realisateur:eq:"+FilmBuilder.REAL_NOM_TMBD_ID_844+":AND";
		l = filmService.search(query, 1, 1, "-titre");
		assertNotNull(l);
		it = l.iterator();
		assertNotNull(it);
		var r = it.next();
		assertNotNull(r);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(r.getTitre());
		query = "acteur:eq:"+FilmBuilder.ACT1_TMBD_ID_844+":AND";
		l = filmService.search(query, 1, 1, "-titre");
		assertNotNull(l);
		it = l.iterator();
		assertNotNull(it);
		var a = it.next();
		assertNotNull(a);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(a.getTitre());
		query = "annee:eq:"+FilmBuilder.ANNEE+":AND";
		l = filmService.search(query, 1, 1, "-titre");
		assertNotNull(l);
		it = l.iterator();
		assertNotNull(it);
		var an = it.next();
		assertNotNull(an);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(an.getTitre());
		query = "allocineFicheFilmId:eq:"+FilmBuilder.ALLOCINE_FICHE_FILM_ID_844+":AND";
		l = filmService.search(query, 1, 1, "-titre");
		assertNotNull(l);
		it = l.iterator();
		assertNotNull(it);
		var allocine = it.next();
		assertNotNull(allocine);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(allocine.getTitre());
		query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_844+":AND";
		l = filmService.search(query, null, null, "");
		assertNotNull(l);
		it = l.iterator();
		assertNotNull(it);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(allocine.getTitre());
	}
	@Test
	public void paginatedSarch() throws ParseException{
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		var query = "titre:eq:"+FilmBuilder.TITRE_FILM_TMBD_ID_844+":AND";
		var page = filmService.paginatedSarch(query, 1, 1, "-titre");
		assertNotNull(page);
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent().size()==1).isTrue();
		var it = page.getContent().iterator();
		assertNotNull(it);
		var f = it.next();
		assertNotNull(f);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(f.getTitre());
		page = filmService.paginatedSarch("", 1, 1, "-titre");
		assertNotNull(page);
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent().size()==1).isTrue();
		it = page.getContent().iterator();
		assertNotNull(it);
		f = it.next();
		assertNotNull(f);
		assertThat(FilmBuilder.TITRE_FILM_TMBD_ID_844).isEqualTo(f.getTitre());
	}
	@Test
	public void saveNewFilm() throws ParseException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, true);
	}
	
	
	@Test
	public void findFilmByTitreWithoutSpecialsCharacters() throws Exception{
		String methodName = "findFilmByTitreWithoutSpecialsCharacters : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_62)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_62)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		String titre = StringUtils.replace(film.getTitre(), ":", "");
		titre = StringUtils.replace(titre, "  ", " ");
		Film retrievedFilm = filmService.findFilmByTitreWithoutSpecialsCharacters(titre);
		FilmBuilder.assertFilmIsNotNull(retrievedFilm, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		logger.debug(methodName + "end");
	}
	
	@Test
	public void findFilmWithAllObjectGraph() throws Exception{
		String methodName = "findFilmWithAllObjectGraph : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		Film retrievedFilm = filmService.findFilm(film.getId());
		FilmBuilder.assertFilmIsNotNull(retrievedFilm,false, FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		logger.debug(methodName + "retrievedFilm ="+retrievedFilm.toString());
		for(Personne acteur : retrievedFilm.getActeur()){
			logger.debug(methodName + " acteur="+acteur.toString());
		}
		for(Personne realisateur : retrievedFilm.getRealisateur()){
			logger.debug(methodName + " realisateur="+realisateur.toString());
		}
		logger.debug(methodName + "end");
	}
	
	@Test
	public void findFilm() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE).setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		film = filmService.findFilm(film.getId());
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(film.getDvd());
	}
	
	@Test
	public void findAllGenres() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, true);
		List<Genre> genres = filmService.findAllGenres();
		assertTrue(CollectionUtils.isNotEmpty(genres));
	}
	
	@Test
	public void findAllDvd() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, null, null, false);
		assertNotNull(filmId3);
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId4 = filmService.saveNewFilm(film4);
		FilmBuilder.assertFilmIsNotNull(film4, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, null, null, false);
		assertNotNull(filmId4);
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.TV)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId5 = filmService.saveNewFilm(film5);
		FilmBuilder.assertFilmIsNotNull(film5, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.TV, null, null, false);
		assertNotNull(filmId5);
		
		Page<Film> dvdFilms = filmService.findAllFilmByOrigine(FilmOrigine.DVD);
		assertNotNull(dvdFilms);
		
		assertTrue(CollectionUtils.isNotEmpty(dvdFilms.getContent()));
		assertTrue("dvdFilms.size() should be 2 but is "+dvdFilms.getContent().size(),dvdFilms.getContent().size()==2);
		for(Film dvd : dvdFilms) {
			FilmBuilder.assertFilmIsNotNull(dvd, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		}
		Page<Film> enSalleFilms = filmService.findAllFilmByOrigine(FilmOrigine.EN_SALLE);
		assertNotNull(enSalleFilms);
		assertTrue(CollectionUtils.isNotEmpty(enSalleFilms.getContent()));
		assertTrue("enSalleFilms.size() should be 2 but is "+enSalleFilms.getContent().size(),enSalleFilms.getContent().size()==2);
		for(Film enSalle : enSalleFilms) {
			FilmBuilder.assertFilmIsNotNull(enSalle, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null, false);
		}
		Page<Film> tvFilms = filmService.findAllFilmByOrigine(FilmOrigine.TV);
		assertNotNull(tvFilms);
		assertTrue(CollectionUtils.isNotEmpty(tvFilms.getContent()));
		assertTrue("tvFilms.size() should be 1 but is "+tvFilms.getContent().size(),tvFilms.getContent().size()==1);
		for(Film tv : tvFilms) {
			FilmBuilder.assertFilmIsNotNull(tv, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.TV, FilmBuilder.FILM_DATE_SORTIE, null, false);
		}
	}
	
	@Test
	public void findAllTmdbFilms() throws Exception {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId);
		Set<Long> tmdbIds = new HashSet<>();
		tmdbIds.add(film.getTmdbId());
		Set<Long> films = filmService.findAllTmdbFilms(tmdbIds);
		assertNotNull(films);
		assertTrue(CollectionUtils.isNotEmpty(films));
		
	}
	
	@Test
	public void findAllRippedFilms() throws Exception {
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
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		List<Film> films = filmService.getAllRippedFilms();
		assertTrue(CollectionUtils.isNotEmpty(films));
		for(Film f : films){
			assertNotNull(f);
		}
	}
	
	@Test
	public void updateFilm() throws ParseException{
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		film.setTitre(FilmBuilder.TITRE_FILM_TMBD_ID_4780);
		Personne real = personneService.buildPersonne(FilmBuilder.REAL_NOM_TMBD_ID_4780, null);
		assertNotNull(real);
		Long idreal = personneService.savePersonne(real);
		assertNotNull(idreal);
		real.setId(idreal);
		film.getRealisateur().clear();
		film.getRealisateur().add(real);
		
		Personne act = personneService.buildPersonne(FilmBuilder.ACT4_TMBD_ID_844, null);
		assertNotNull(act);
		Long idAct = personneService.savePersonne(act);
		assertNotNull(idAct);
		act.setId(idAct);
		film.getActeur().clear();
		film.getActeur().add(act);
		
		final String posterPath = "posterPath";
		film.setPosterPath(posterPath);
		Film filmUpdated = filmService.updateFilm(film);
		//Film filmUpdated = filmService.findFilm(film.getId());
		
		assertNotNull(filmUpdated);
		//FilmBuilder.assertFilmIsNotNull(filmUpdated, true,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD);
		assertThat(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780)).isEqualTo(filmUpdated.getTitre());
		assertThat(FilmBuilder.REAL_NOM_TMBD_ID_4780).isEqualTo(filmUpdated.getRealisateur().iterator().next().getNom());
		assertThat(FilmBuilder.ACT4_TMBD_ID_844).isEqualTo(filmUpdated.getActeur().iterator().next().getNom());
		assertThat(FilmBuilder.ACT4_TMBD_ID_844).isEqualTo(filmUpdated.getActeur().iterator().next().getNom());
		assertThat(posterPath).isEqualTo(filmUpdated.getPosterPath());
	}
	
	@Test
	public void transfertEnSalleToDvdFilm() throws Exception {
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
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null, false);
		film.setOrigine(FilmOrigine.DVD);
		Film filmUpdated = filmService.updateFilm(film);
		FilmBuilder.assertFilmIsNotNull(filmUpdated, true,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		assertThat(FilmOrigine.DVD).isEqualTo(filmUpdated.getOrigine());
		
		assertEquals(Integer.valueOf(2), filmUpdated.getDvd().getZone());
		assertTrue(filmUpdated.getDvd().isRipped());
	}
	@Test
	public void cleanAllFilms() throws ParseException {
		String methodName = "cleanAllFilms : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, true);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		assertNotNull(filmId3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		filmService.cleanAllFilms();
		
		//assertTrue(CollectionUtils.isEmpty(filmService.findAllFilms(null)));
		logger.debug(methodName + "end");
	}
	
	@Test
	public void findAllFilmsByCriteriaFilmOrigineService() throws ParseException {
		String methodName = "findAllFilmsByCriteriaFilmOrigineService : ";
		logger.debug(methodName + "start");
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film dvdFilm = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long dvdFilmId = filmService.saveNewFilm(dvdFilm);
		assertNotNull(dvdFilmId);
		FilmBuilder.assertFilmIsNotNull(dvdFilm, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film enSalleFilm = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long enSalleFilmId = filmService.saveNewFilm(enSalleFilm);
		assertNotNull(enSalleFilmId);
		FilmBuilder.assertFilmIsNotNull(enSalleFilm, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null, false);
		List<Film> films = filmService.findFilmByOrigine(FilmOrigine.EN_SALLE);
		assertNotNull(films);
		for(Film f : films){
			logger.debug(f.toString());
		}
		assertEquals(1, films.size());
		assertThat(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780)).isEqualTo(films.get(0).getTitre());
		
		logger.debug(methodName + "end");
	}
	@Test
	public void findAllFilmsByCriteriaActeur() throws ParseException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Long selectedActeurId = film.getActeur().iterator().next().getId();
		List<Film> films = filmDao.findFilmByActeur(film.getActeur().iterator().next());
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(1, films.size());
		Film f2 = films.get(0);
		assertNotNull(f2);
		assertNotNull(f2.getActeur());
		Optional<Personne> op = f2.getActeur().stream().filter(acteurDto -> acteurDto.getId().equals(selectedActeurId)).findAny();
		Personne acteur = op.get();
		assertNotNull(acteur);
		assertEquals(selectedActeurId, acteur.getId());
	}
	@Test
	public void findAllFilmsByCriteriaDao() throws ParseException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		List<Film> films = filmDao.findFilmByTitre(FilmBuilder.TITRE_FILM_TMBD_ID_844);
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(1, films.size());
		Film f2 = films.get(0);
		assertThat(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844)).isEqualTo(f2.getTitre());
		
		Set<Personne> realisateurSet = f2.getRealisateur();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		Personne real = personneService.findByPersonneId(realisateur.getId());
		assertThat(real.getNom()).isEqualTo(realisateur.getNom());
		assertThat(real.getPrenom()).isEqualTo(realisateur.getPrenom());
		
	}
	@Test
	//@Disabled
	public void findAllFilmsByCriteriaRippedSinceDao() throws ParseException {
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
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setRipped(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET2))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET2, FilmOrigine.DVD, null, null, false);
		final String query = "origine:eq:"+FilmOrigine.DVD+":AND";
		List<Film> films = filmService.search(query, 1, 1, "-titre");
		assertNotNull(films);
		for(Film f2 : films){
			logger.debug(f2.toString());
		}
		assertEquals(1, films.size());
		Film f2 = films.get(0);
		assertThat(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780)).isEqualTo(f2.getTitre());
		Set<Personne> realisateurSet = f2.getRealisateur();
		assertNotNull(realisateurSet);
		assertEquals(1,realisateurSet.size());
		Personne realisateur = realisateurSet.iterator().next();
		assertNotNull(realisateur);
		Personne real = personneService.findByPersonneId(realisateur.getId());
		assertThat(real.getNom()).isEqualTo(realisateur.getNom());
		assertThat(real.getPrenom()).isEqualTo(realisateur.getPrenom());
	}
	@Test
	public void removeFilmDao() throws ParseException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		Personne real = film.getRealisateur().iterator().next();
		assertNotNull(real);
		filmService.removeFilm(film);
		Exception exception = assertThrows(FilmNotFoundException.class, () -> {
			filmService.findFilm(filmId);
	    });

	    String expectedMessage = String.format("film with id %s not found", filmId);
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	    
	}
	
	@Test
	public void checkIfTmdbFilmExists() throws ParseException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, true);
		Boolean exists = filmService.checkIfTmdbFilmExists(film.getTmdbId());
		assertTrue(exists);
	}
	
	@Test
	public void testExcelToCsv() throws IOException, ParseException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		assertNotNull(filmId2);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		assertNotNull(filmId3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		
		List<Film> list = filmDao.findAll();
		assertNotNull(list);
		assertTrue(CollectionUtils.isNotEmpty(list));
	    byte[] excelContent = this.excelFilmHandler.createByteContentFromFilmList(list);
	    assertNotNull(excelContent);
	    Workbook workBook = this.excelFilmHandler.createSheetFromByteArray(excelContent);
	    assertNotNull(workBook);
		workBook.forEach(sheet -> {
			assertThat(FilmBuilder.SHEET_NAME).isEqualTo(sheet.getSheetName());
        });
		Sheet sheet = workBook.getSheetAt(0);
        assertThat(FilmBuilder.SHEET_NAME).isEqualTo(sheet.getSheetName());
        String csv = this.excelFilmHandler.createCsvFromExcel(workBook);
        assertNotNull(csv);
	}
	@Test
	public void testCreateSXSSFWorkbookFromFilmList() throws IOException, ParseException {
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setRipped(true)
				.setVu(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_4780)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_4780)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_4780)
				.setRipped(true)
				.setVu(true)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_4780)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId2 = filmService.saveNewFilm(film2);
		assertNotNull(filmId2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, null, null, false);
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_1271)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_1271)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_1271)
				.setRipped(true)
				.setVu(false)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE).setDateInsertion(FilmBuilder.FILM_DATE_INSERTION)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setZone(Integer.valueOf(2))
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_1271)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET)).setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE2)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844).build();
		Long filmId3 = filmService.saveNewFilm(film3);
		assertNotNull(filmId3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, null, null, false);
		
		Page<Film> films = filmService.paginatedSarch(null, 1, 10, "+titre");
		List<Film> list = films.getContent();
		assertNotNull(list);
		assertTrue(CollectionUtils.isNotEmpty(list));
		byte[] excelContent = this.excelFilmHandler.createByteContentFromFilmList(list);
		assertNotNull(excelContent);
		Workbook workBook = excelFilmHandler.createSheetFromByteArray(excelContent);
		assertNotNull(workBook);
		workBook.forEach(sheet -> {
        	assertEquals(FilmBuilder.SHEET_NAME, sheet.getSheetName());
        });
		Sheet sheet = workBook.getSheetAt(0);
        assertEquals(FilmBuilder.SHEET_NAME, sheet.getSheetName());
        DataFormatter dataFormatter = new DataFormatter();
        sheet.forEach(row -> {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",Locale.FRANCE);
        	if(row.getRowNum()==1) {
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_844, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_844), StringUtils.upperCase(cellValue));
                    }
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE, Integer.valueOf(cellValue));
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertTrue(cellValue.contains(FilmBuilder.ACT1_TMBD_ID_844));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT2_TMBD_ID_844));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT3_TMBD_ID_844));
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	assertEquals(FilmBuilder.ZONE_DVD, cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==11) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==12) {
                    	assertEquals(DvdFormat.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==13) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	Date sortie = null;
                    	try {
							sortie = sdf.parse(FilmBuilder.DVD_DATE_SORTIE);
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(df.print(sortie, Locale.FRANCE), cellValue);
                    }
                });
        	}else if(row.getRowNum()==2) {
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_1271, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_1271), StringUtils.upperCase(cellValue));
                    }
                    
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE, Integer.valueOf(cellValue));
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertTrue(cellValue.contains(FilmBuilder.ACT1_TMBD_ID_1271));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT2_TMBD_ID_1271));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT3_TMBD_ID_1271));
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.EN_SALLE.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("non", cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	assertEquals(StringUtils.EMPTY, cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals(StringUtils.EMPTY, cellValue);
                    }
                    if(cell.getColumnIndex()==11) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==12) {
                    	assertEquals(StringUtils.EMPTY, cellValue);
                    }
                    if(cell.getColumnIndex()==13) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	Date sortie = null;
                    	try {
							sortie = sdf.parse(FilmBuilder.DVD_DATE_SORTIE2);
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(df.print(sortie, Locale.FRANCE), cellValue);
                    }
                });
        	}else if(row.getRowNum()==3) {
        		row.forEach(cell -> {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cell.getColumnIndex()==0) {
                    	assertEquals(FilmBuilder.REAL_NOM_TMBD_ID_4780, cellValue);
                    }
                    if(cell.getColumnIndex()==1) {
                    	assertEquals(StringUtils.upperCase(FilmBuilder.TITRE_FILM_TMBD_ID_4780), StringUtils.upperCase(cellValue));
                    }
                    if(cell.getColumnIndex()==2) {
                    	assertEquals(FilmBuilder.ANNEE, Integer.valueOf(cellValue));
                    }
                    if(cell.getColumnIndex()==3) {
                    	assertTrue(cellValue.contains(FilmBuilder.ACT3_TMBD_ID_4780));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT1_TMBD_ID_4780));
                    	assertTrue(cellValue.contains(FilmBuilder.ACT2_TMBD_ID_4780));
                    }
                    if(cell.getColumnIndex()==4) {
                    	assertEquals(FilmOrigine.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==5) {
                    	assertEquals(FilmBuilder.TMDBID_844.toString(), cellValue);
                    }
                    if(cell.getColumnIndex()==6) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==8) {
                    	final SimpleDateFormat sdfInsert = new SimpleDateFormat("yyyy/MM/dd");
                    	String dateInsertion=null;
                    	try {
                    		dateInsertion=FilmBuilder.createDateInsertion(sdfInsert.parse(FilmBuilder.FILM_DATE_INSERTION), "dd/MM/yyyy");
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(dateInsertion, cellValue);
                    }
                    if(cell.getColumnIndex()==9) {
                    	assertEquals(FilmBuilder.ZONE_DVD, cellValue);
                    }
                    if(cell.getColumnIndex()==10) {
                    	assertEquals("oui", cellValue);
                    }
                    if(cell.getColumnIndex()==11) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	assertEquals(df.print(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET),Locale.FRANCE), cellValue);
                    }
                    if(cell.getColumnIndex()==12) {
                    	assertEquals(DvdFormat.DVD.name(), cellValue);
                    }
                    if(cell.getColumnIndex()==13) {
                    	final DateFormatter df = new DateFormatter("dd/MM/yyyy");
                    	Date sortie = null;
                    	try {
							sortie = sdf.parse(FilmBuilder.DVD_DATE_SORTIE);
						} catch (ParseException e) {
							e.printStackTrace();
						}
                    	assertEquals(df.print(sortie, Locale.FRANCE), cellValue);
                    }
                });
        	}
        });
	}
	
	@Test
	public void findAllLastAddedFilmsByOrigine() throws ParseException {
		final int rowNumber = 3;
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		final String dateInsertion1 = "2014/08/01";
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion1)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		final String dateInsertion2 = "2014/09/01";
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion2)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId2);
		final String dateInsertion3 = "2014/10/01";
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion3)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId3 = filmService.saveNewFilm(film3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId3);
		final String dateInsertion4 = "2014/11/01";
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion4)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.EN_SALLE)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId4 = filmService.saveNewFilm(film4);
		FilmBuilder.assertFilmIsNotNull(film4, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.EN_SALLE, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId4);
		final String dateInsertion5 = "2014/12/01";
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion5)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.TV)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId5 = filmService.saveNewFilm(film5);
		FilmBuilder.assertFilmIsNotNull(film5, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.TV, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId5);
		
		var query = "origine:eq:"+FilmOrigine.DVD+":AND";
		var page = filmService.paginatedSarch(query, 1, 10, "-dateInsertion");
		assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		assertTrue("list should be equals to "+rowNumber,page.getContent().size()==rowNumber);
		Film f1 = page.getContent().get(0);
		assertEquals(film3, f1);
		Film f2 = page.getContent().get(1);
		assertEquals(film2, f2);
		Film f3 = page.getContent().get(2);
		assertEquals(film, f3);
	}
	
	@Test
	public void findAllLastNotSeenAddedFilmsByOrigine() throws ParseException {
		final int rowNumber = 2;
		Genre genre1 = filmService.saveGenre(new Genre(28,"Action"));
		Genre genre2 = filmService.saveGenre(new Genre(35,"Comedy"));
		final String dateInsertion1 = "2014/08/01";
		Film film = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion1)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.FALSE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId = filmService.saveNewFilm(film);
		assertNotNull(filmId);
		FilmBuilder.assertFilmIsNotNull(film, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		final String dateInsertion2 = "2014/09/01";
		Film film2 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_4780)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion2)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId2 = filmService.saveNewFilm(film2);
		FilmBuilder.assertFilmIsNotNull(film2, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId2);
		final String dateInsertion3 = "2014/10/01";
		Film film3 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_TMBD_ID_1271)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion3)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId3 = filmService.saveNewFilm(film3);
		FilmBuilder.assertFilmIsNotNull(film3, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId3);
		final String dateInsertion4 = "2014/11/01";
		Film film4 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion4)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.TRUE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId4 = filmService.saveNewFilm(film4);
		FilmBuilder.assertFilmIsNotNull(film4, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId4);
		final String dateInsertion5 = "2014/12/01";
		Film film5 = new FilmBuilder.Builder(FilmBuilder.TITRE_FILM_REREREUPDATED)
				.setTitreO(FilmBuilder.TITRE_FILM_TMBD_ID_844)
				.setAct1Nom(FilmBuilder.ACT1_TMBD_ID_844)
				.setAct2Nom(FilmBuilder.ACT2_TMBD_ID_844)
				.setAct3Nom(FilmBuilder.ACT3_TMBD_ID_844)
				.setAnnee(FilmBuilder.ANNEE)
				.setDateSortie(FilmBuilder.FILM_DATE_SORTIE)
				.setDateInsertion(dateInsertion5)
				.setDvdFormat(DvdFormat.DVD)
				.setOrigine(FilmOrigine.DVD)
				.setVu(Boolean.FALSE.booleanValue())
				.setGenre1(genre1)
				.setGenre2(genre2)
				.setRealNom(FilmBuilder.REAL_NOM_TMBD_ID_844)
				.setRipDate(FilmBuilder.createRipDate(FilmBuilder.RIP_DATE_OFFSET))
				.setDvdDateSortie(FilmBuilder.DVD_DATE_SORTIE)
				.setAllocineFicheFilmId(FilmBuilder.ALLOCINE_FICHE_FILM_ID_844)
				.build();
		Long filmId5 = filmService.saveNewFilm(film5);
		FilmBuilder.assertFilmIsNotNull(film5, false,FilmBuilder.RIP_DATE_OFFSET, FilmOrigine.DVD, FilmBuilder.FILM_DATE_SORTIE, null, false);
		assertNotNull(filmId5);
		var query = "origine:eq:"+FilmOrigine.DVD+":AND,vu:eq:"+Boolean.FALSE+":AND";
		var page = filmService.paginatedSarch(query, 1, 10, "-dateInsertion");
		assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		assertTrue("list should be equals to "+rowNumber,page.getContent().size()==rowNumber);
		Film f1 = page.getContent().get(0);
		assertEquals(film5, f1);
		Film f2 = page.getContent().get(1);
		assertEquals(film, f2);
	}
}
