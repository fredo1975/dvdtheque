package fr.fredos.dvdtheque.rest.dao.model.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.common.utils.DateUtils;
import fr.fredos.dvdtheque.rest.dao.domain.Dvd;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;

public class FilmBuilder {
	public static final Integer ALLOCINE_FICHE_FILM_ID_844 = 1;
	public static final Integer ALLOCINE_FICHE_FILM_ID_4780 = 2;
	public static final String TITRE_FILM_TMBD_ID_844 = "2046";
	public static final String TITRE_FILM_TMBD_ID_62 = "2001 : L'ODYSSÉE DE L'ESPACE";
	public static final String TITRE_FILM_TMBD_ID_4780 = "OBSESSION";
	public static final String TITRE_FILM_TMBD_ID_1271 = "300";
	public static final String TITRE_FILM_TMBD_ID_10315 = "FANTASTIC MR. FOX";
	public static final String TITRE_FILM_FOR_SEARCH_BY_TITRE = "vacances";
	public static final String TITRE_FILM_REREUPDATED = "Again Lorem Ipsum rereupdated";
	public static final String TITRE_FILM_REREREUPDATED = "Another Lorem Ipsum rerereupdated";
	public static final Integer ANNEE = 2015;
	public static final String FILM_DATE_SORTIE = "2015/08/01";
	public static final String FILM_DATE_INSERTION = "2019/08/01";
	public static final String FILM_DATE_VU = "2019-08-01";
	public static final String DVD_DATE_SORTIE = "2015/12/01";
	public static final String REAL_NOM_TMBD_ID_844 = "WONG KAR-WAI";
	public static final String REAL_NOM_TMBD_ID_4780 = "BRIAN DE PALMA";
	public static final String REAL_NOM_TMBD_ID_1271 = "ZACK SNYDER";
	public static final String ACT1_TMBD_ID_844 = "TONY LEUNG CHIU-WAI";
	public static final String ACT2_TMBD_ID_844 = "MAGGIE CHEUNG";
	public static final String ACT3_TMBD_ID_844 = "DONG JIE";
	public static final String ACT4_TMBD_ID_844 = "ZHANG ZIYI";
	public static final String ACT1_TMBD_ID_4780 = "JOHN LITHGOW";
	public static final String ACT2_TMBD_ID_4780 = "J. PATRICK MCNAMARA";
	public static final String ACT3_TMBD_ID_4780 = "CLIFF ROBERTSON";
	public static final String ACT4_TMBD_ID_4780 = "GENEVIÈVE BUJOLD";
	public static final String ACT1_TMBD_ID_1271 = "LENA HEADEY";
	public static final String ACT2_TMBD_ID_1271 = "DAVID WENHAM";
	public static final String ACT3_TMBD_ID_1271 = "MICHAEL FASSBENDER";
	public static final String ACT4_TMBD_ID_1271 = "TOM WISDOM";
	public static final String SHEET_NAME = "Films";
	public static final String ZONE_DVD = "2";
	public static final Long TMDBID_844 = Long.valueOf(844);
	public static final int RIP_DATE_OFFSET = -10;
	public static final int RIP_DATE_OFFSET2 = -1;
	public static Long tmdbId1 = Long.valueOf(1271);
	public static final String TMDBID1_DATE_SORTIE = "2007/03/21";
	public static Long tmdbId2 = Long.valueOf(844);
	public static final String TMDBID2_DATE_SORTIE = "2004/05/20";
	public static Long tmdbId3 = Long.valueOf(4780);
	public static final String TMDBID3_DATE_SORTIE = "2007/01/07";
	
	public static Date createRipDate(int ripDateOffset) {
		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return org.apache.commons.lang.time.DateUtils.addDays(cal.getTime(), ripDateOffset);
	}
	public static class Builder {
		private String titre;
		private String titreO;
		private Integer annee;
		private Date dateSortie;
		private Date dateInsertion;
		private Date dateSortieDvd;
		private String realNom;
		private String act1Nom;
		private String act2Nom;
		private String act3Nom;
		private Date ripDate; 
		private DvdFormat dvdFormat;
		private Integer zone;
		private Genre genre1; 
		private Genre genre2;
		private boolean ripped;
		private boolean vu;
		private FilmOrigine origine;
		private String posterPath;
		private Integer allocineFicheFilmId;
		private LocalDate dateVue;
		public Builder(String titre) {
            this.titre = titre;
        }
		public Builder setTitreO(String titreO) {
			this.titreO = titreO;
			return this;
		}
		public Builder setAnnee(Integer annee) {
			this.annee = annee;
			return this;
		}
		public Builder setDateSortie(String dateSortie) throws ParseException {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			this.dateSortie = sdf.parse(dateSortie);
			return this;
		}
		public Builder setDateInsertion(String dateInsertion) throws ParseException {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			this.dateInsertion = sdf.parse(dateInsertion);
			return this;
		}
		public Builder setRealNom(String realNom) {
			this.realNom = realNom;
			return this;
		}
		public Builder setAct1Nom(String act1Nom) {
			this.act1Nom = act1Nom;
			return this;
		}
		public Builder setAct2Nom(String act2Nom) {
			this.act2Nom = act2Nom;
			return this;
		}
		public Builder setAct3Nom(String act3Nom) {
			this.act3Nom = act3Nom;
			return this;
		}
		public Builder setRipDate(Date ripDate) {
			this.ripDate = ripDate;
			return this;
		}
		public Builder setDvdFormat(DvdFormat dvdFormat) {
			this.dvdFormat = dvdFormat;
			return this;
		}
		public Builder setGenre1(Genre genre1) {
			this.genre1 = genre1;
			return this;
		}
		public Builder setGenre2(Genre genre2) {
			this.genre2 = genre2;
			return this;
		}
		public Builder setZone(Integer zone) {
			this.zone = zone;
			return this;
		}
		public Builder setRipped(boolean ripped) {
			this.ripped = ripped;
			return this;
		}
		public Builder setDateSortieDvd(String dateSortie) throws ParseException {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			this.dateSortieDvd = sdf.parse(dateSortie);
			return this;
		}
		public Builder setVu(boolean vu) {
			this.vu = vu;
			return this;
		}
		public Builder setDateVue(LocalDate dateVue) {
			this.dateVue = dateVue;
			return this;
		}
		public Builder setOrigine(FilmOrigine origine) {
			this.origine = origine;
			return this;
		}
		
		public Builder setPosterPath(String posterPath) {
			this.posterPath = posterPath;
			return this;
		}
		
		public Builder setAllocineFicheFilmId(Integer allocineFicheFilmId) {
			this.allocineFicheFilmId = allocineFicheFilmId;
			return this;
		}
		public Film build() {
			Film film = new Film();
			Set<Personne> realisateurs = new HashSet<>();
			Set<Personne> acteurs = new HashSet<>();
			Set<Genre> genres = new HashSet<>();
			Personne real = new Personne();
			real.setNom(this.realNom);
			realisateurs.add(real);
			if(StringUtils.isNotEmpty(act1Nom)) {
				Personne act1 = new Personne();
				act1.setNom(act1Nom);
				acteurs.add(act1);
			}
			if(StringUtils.isNotEmpty(act2Nom)) {
				Personne act2 = new Personne();
				act2.setNom(act2Nom);
				acteurs.add(act2);
			}
			if(StringUtils.isNotEmpty(act3Nom)) {
				Personne act3 = new Personne();
				act3.setNom(act3Nom);
				acteurs.add(act3);
			}
			film.setTitre(this.titre);
			film.setTitreO(this.titreO);
			film.setAnnee(this.annee);
			film.setDateSortie(this.dateSortie);
			film.setDateInsertion(this.dateInsertion);
			film.setDateSortieDvd(this.dateSortieDvd);
			film.setDateVue(this.dateVue);
			film.setRealisateur(realisateurs);
			film.setActeur(acteurs);
			genres.add(this.genre1);
			genres.add(this.genre2);
			film.setGenre(genres);
			if(this.ripDate!=null || this.dvdFormat!=null || this.zone!=null || this.ripped==true) {
				Dvd dvd = new Dvd();
				dvd.setDateRip(this.ripDate);
				dvd.setFormat(this.dvdFormat);
				dvd.setZone(this.zone);
				dvd.setRipped(this.ripped);
				film.setDvd(dvd);
			}
			
			film.setOrigine(this.origine);
			film.setVu(this.vu);
			// hard coded
			film.setTmdbId(TMDBID_844);
			film.setOverview("Overview");
			film.setPosterPath(this.posterPath);
			film.setAllocineFicheFilmId(allocineFicheFilmId);
			return film;
		}
	}
	public static void assertCacheSize(final int mapActeursByOrigineSize, final int mapRealisateursByOrigineSize,
			final FilmDisplayTypeParam filmDisplayTypeParam, 
			final List<Personne> acteursList, 
			final List<Personne> realisateursList) {
		assertEquals(mapActeursByOrigineSize, acteursList.size());
		assertEquals(mapRealisateursByOrigineSize, realisateursList.size());
	}
	public static void assertFilmIsNotNull(final Film film, 
			final boolean dateRipNull, 
			final int ripDateOffset, 
			final FilmOrigine filmOrigine, 
			final String filmDateSortie, 
			final String filmDateInsertion, 
			final boolean isNull) throws ParseException {
		assertNotNull("film should exists",film);
		assertNotNull("film should have an id",film.getId());
		assertNotNull("film should have a titre",film.getTitre());
		assertNotNull("film should have a année",film.getAnnee());
		assertNotNull("film should have a date sortie",film.getDateSortie());
		assertNotNull("film should have a date insertion",film.getDateInsertion());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",Locale.FRANCE);
		if(StringUtils.isNotEmpty(filmDateSortie)) {
			Date _filmDateSortie = DateUtils.clearDate(sdf.parse(filmDateSortie));
			assertEquals("film date sortie should match",film.getDateSortie(), _filmDateSortie);
		}else {
			Date _filmDateSortie = DateUtils.clearDate(sdf.parse(FILM_DATE_SORTIE));
			assertEquals("film date sortie should match",film.getDateSortie(), _filmDateSortie);
		}
		/*
		if(StringUtils.isNotEmpty(filmDateInsertion)) {
			Date _filmDateInsertion = DateUtils.clearDate(sdf.parse(filmDateInsertion));
			assertEquals("film date insertion should match",film.getDateInsertion(), _filmDateInsertion);
		}else {
			Date _filmDateInsertion = DateUtils.clearDate(sdf.parse(FILM_DATE_INSERTION));
			assertEquals("film date insertion should match",film.getDateInsertion(), _filmDateInsertion);
		}*/
		if(film.getDateSortieDvd() != null) {
			Date dvdDateSortie = sdf.parse(DVD_DATE_SORTIE);
			assertEquals("dvd date sortie should match",film.getDateSortieDvd(), dvdDateSortie);
		}
		if(FilmOrigine.DVD == filmOrigine) {
			assertNotNull("dvd Should exists",film.getDvd());
			if (!dateRipNull) {
				assertEquals("now -10 days should match",DateUtils.clearDate(createRipDate(ripDateOffset)), film.getDvd().getDateRip());
			}
		}
		assertTrue("genres should exists",CollectionUtils.isNotEmpty(film.getGenre()));
		assertTrue("actors should exists",CollectionUtils.isNotEmpty(film.getActeur()));
		assertTrue("there should be at least 3 actors",film.getActeur().size() >= 3);
		assertTrue("realisateur should exists",CollectionUtils.isNotEmpty(film.getRealisateur()));
		assertTrue("should be 1 realisateur",film.getRealisateur().size() == 1);
		assertAllocineFicheFilmIdIsNull(film, isNull);
		
	}
	public static void assertAllocineFicheFilmIdIsNull(final Film film,  final boolean isNull) {
		if(isNull) {
			assertTrue("AllocineFicheFilmId should not be null",film.getAllocineFicheFilmId() == null);
		}else {
			assertTrue("AllocineFicheFilmId should not be null",film.getAllocineFicheFilmId() != null);
		}
		
	}
	public static String createDateInsertion(Date dateInsertion, String pattern) {
		String resultPattern=null;
		if(StringUtils.isEmpty(pattern)) {
			resultPattern = "yyyy/MM/dd";
		}else {
			resultPattern = pattern;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(resultPattern);
		if(dateInsertion == null) {
			return sdf.format(new Date());
		}
		return sdf.format(dateInsertion);
	}
}
