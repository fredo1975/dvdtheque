package fr.fredos.dvdtheque.dao.model.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class FilmBuilder {
	public static final String TITRE_FILM_TMBD_ID_844 = "2046";
	public static final String TITRE_FILM_TMBD_ID_4780 = "OBSESSION";
	public static final String TITRE_FILM_TMBD_ID_1271 = "300";
	public static final String TITRE_FILM_REREUPDATED = "Again Lorem Ipsum rereupdated";
	public static final String TITRE_FILM_REREREUPDATED = "Another Lorem Ipsum rerereupdated";
	public static final Integer ANNEE = 2015;
	public static final String DATE_SORTIE = "2015/08/01";
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
	public static final Long TMDBID_844 = new Long(100);
	public static final int RIP_DATE_OFFSET = -10;
	public static final int RIP_DATE_OFFSET2 = -1;
	public static Date createRipDate(int ripDateOffset) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		return DateUtils.addDays(cal.getTime(), ripDateOffset);
	}
	public static class Builder {
		private String titre;
		private String titreO;
		private Integer annee;
		private Date dateSortie;
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
		public Builder setVu(boolean vu) {
			this.vu = vu;
			return this;
		}
		public Builder setOrigine(FilmOrigine origine) {
			this.origine = origine;
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
			film.setRealisateurs(realisateurs);
			film.setActeurs(acteurs);
			genres.add(this.genre1);
			genres.add(this.genre2);
			film.setGenres(genres);
			Dvd dvd = new Dvd();
			dvd.setDateRip(this.ripDate);
			dvd.setFormat(this.dvdFormat);
			dvd.setZone(this.zone);
			film.setDvd(dvd);
			dvd.setRipped(this.ripped);
			film.setOrigine(this.origine);
			film.setVu(this.vu);
			// hard coded
			film.setTmdbId(TMDBID_844);
			film.setOverview("Overview");
			return film;
		}
	}
	
	public static void assertFilmIsNotNull(Film film, boolean dateRipNull, int ripDateOffset, boolean isOrigineDvd) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDateSortie());
		if(isOrigineDvd) {
			assertNotNull(film.getDvd());
			if (!dateRipNull) {
				assertEquals(clearDate(createRipDate(ripDateOffset)), film.getDvd().getDateRip());
			}
		}
		assertTrue(CollectionUtils.isNotEmpty(film.getGenres()));
		
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size() >= 3);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size() == 1);
	}
	public static Date clearDate(Date dateToClear) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToClear);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		return cal.getTime();
	}
}
