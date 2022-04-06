package fr.fredos.dvdtheque.batch.csv.format;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FilmCsvImportFormat {
	public FilmCsvImportFormat() {
		super();
	}
	private String realisateur;
	private String titre;
	private String origine;
	private Integer zonedvd;
	private Integer annee;
	private String acteurs;
	private String ripped;
	private String ripDate;
	private String dateSortieDvd;
	private String dateInsertion;
	private String filmFormat;
	private Long tmdbId;
	private String vu;
	private LocalDateTime dateMaj;
	private String dateVue;
	public String getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(String realisateur) {
		this.realisateur = realisateur;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public Integer getZonedvd() {
		return zonedvd;
	}
	public void setZonedvd(Integer zonedvd) {
		this.zonedvd = zonedvd;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	public String getActeurs() {
		return acteurs;
	}
	public void setActeurs(String acteurs) {
		this.acteurs = acteurs;
	}
	public String getRipped() {
		return ripped;
	}
	public void setRipped(String ripped) {
		this.ripped = ripped;
	}
	public String getRipDate() {
		return ripDate;
	}
	public void setRipDate(String ripDate) {
		this.ripDate = ripDate;
	}
	public String getFilmFormat() {
		return filmFormat;
	}
	public void setFilmFormat(String filmFormat) {
		this.filmFormat = filmFormat;
	}
	public Long getTmdbId() {
		return tmdbId;
	}
	public void setTmdbId(Long tmdbId) {
		this.tmdbId = tmdbId;
	}
	public String getVu() {
		return vu;
	}
	public void setVu(String vu) {
		this.vu = vu;
	}
	public String getOrigine() {
		return origine;
	}
	public void setOrigine(String origine) {
		this.origine = origine;
	}
	public String getDateSortieDvd() {
		return dateSortieDvd;
	}
	public void setDateSortieDvd(String dateSortieDvd) {
		this.dateSortieDvd = dateSortieDvd;
	}
	public String getDateInsertion() {
		return dateInsertion;
	}
	public void setDateInsertion(String dateInsertion) {
		this.dateInsertion = dateInsertion;
	}
	public LocalDateTime getDateMaj() {
		return dateMaj;
	}
	public void setDateMaj(LocalDateTime dateMaj) {
		this.dateMaj = dateMaj;
	}
	public String getDateVue() {
		return dateVue;
	}
	public void setDateVue(String dateVue) {
		this.dateVue = dateVue;
	}
	@Override
	public String toString() {
		return "FilmCsvImportFormat [realisateur=" + realisateur + ", titre=" + titre + ", origine=" + origine
				+ ", zonedvd=" + zonedvd + ", annee=" + annee + ", acteurs=" + acteurs + ", ripped=" + ripped
				+ ", ripDate=" + ripDate + ", dateSortieDvd=" + dateSortieDvd + ", dateInsertion=" + dateInsertion
				+ ", filmFormat=" + filmFormat + ", tmdbId=" + tmdbId + ", vu=" + vu + ", dateMaj=" + dateMaj
				+ ", dateVue=" + dateVue + "]";
	}
}
