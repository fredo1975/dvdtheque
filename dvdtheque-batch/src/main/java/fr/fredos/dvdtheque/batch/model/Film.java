package fr.fredos.dvdtheque.batch.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import fr.fredos.dvdtheque.common.enums.FilmOrigine;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Film implements Serializable, Comparable<Film>{
	private static final long serialVersionUID = 1L;
	private Long id;
	private Integer annee;
	private Date dateSortie;
	private Date dateInsertion;
	private Date dateSortieDvd;
	private String titre;
	private String titreO;
	private Dvd dvd;
	private FilmOrigine origine;
	private Set<Personne> realisateur = new HashSet<>();
	private Set<Personne> acteur = new HashSet<>();
	private Set<CritiquesPresse> critiquesPresse = new TreeSet<>();
	private boolean vu;
	private String posterPath;
	private Long tmdbId;
	private String overview;
	private Integer runtime;
	private Set<Genre> genre = new HashSet<>();
	private String homepage;
	private boolean alreadyInDvdtheque;
	@JsonDeserialize(using = LocalDateDeserializer.class)  
	@JsonSerialize(using = LocalDateSerializer.class)  
	private LocalDate dateVue;
	public Film() {
		super();
	}
	public Film(Long id) {
		super();
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public String getTitreO() {
		return titreO;
	}
	public void setTitreO(String titreO) {
		this.titreO = titreO;
	}
	public Dvd getDvd() {
		return dvd;
	}
	public void setDvd(Dvd dvd) {
		this.dvd = dvd;
	}
	public Set<Personne> getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(Set<Personne> realisateur) {
		this.realisateur = realisateur;
	}
	public Set<Genre> getGenre() {
		return genre;
	}
	public void setGenre(Set<Genre> genre) {
		this.genre = genre;
	}
	public Set<Personne> getActeur() {
		return acteur;
	}
	public void setActeur(Set<Personne> acteur) {
		this.acteur = acteur;
	}
	public Set<CritiquesPresse> getCritiquesPresse() {
		return critiquesPresse;
	}
	public void setCritiquesPresse(Set<CritiquesPresse> critiquesPresse) {
		this.critiquesPresse = critiquesPresse;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public boolean isVu() {
		return vu;
	}
	public void setVu(boolean vu) {
		this.vu = vu;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public FilmOrigine getOrigine() {
		return origine;
	}
	public void setOrigine(FilmOrigine origine) {
		this.origine = origine;
	}
	public Date getDateSortie() {
		return dateSortie;
	}
	public void setDateSortie(Date dateSortie) {
		this.dateSortie = dateSortie;
	}
	public Date getDateInsertion() {
		return dateInsertion;
	}
	public void setDateInsertion(Date dateInsertion) {
		this.dateInsertion = dateInsertion;
	}
	public LocalDate getDateVue() {
		return dateVue;
	}
	public void setDateVue(LocalDate dateVue) {
		this.dateVue = dateVue;
	}
	public Date getDateSortieDvd() {
		return dateSortieDvd;
	}
	public void setDateSortieDvd(Date dateSortieDvd) {
		this.dateSortieDvd = dateSortieDvd;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Film other = (Film) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public String getPosterPath() {
		return posterPath;
	}
	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}
	public Long getTmdbId() {
		return tmdbId;
	}
	public void setTmdbId(Long tmdbId) {
		this.tmdbId = tmdbId;
	}
	public boolean isAlreadyInDvdtheque() {
		return alreadyInDvdtheque;
	}
	public void setAlreadyInDvdtheque(boolean alreadyInDvdtheque) {
		this.alreadyInDvdtheque = alreadyInDvdtheque;
	}
	public Integer getRuntime() {
		return runtime;
	}
	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public String toString() {
		return "Film [id=" + id + ", annee=" + annee + ", dateSortie=" + dateSortie + ", dateInsertion=" + dateInsertion
				+ ", titre=" + titre + ", titreO=" + titreO + ", dvd=" + dvd + ", origine=" + origine + ", realisateur="
				+ realisateur + ", acteur=" + acteur + ", critiquesPresse=" + critiquesPresse + ", vu=" + vu
				+ ", posterPath=" + posterPath + ", tmdbId=" + tmdbId + ", overview=" + overview + ", runtime="
				+ runtime + ", genre=" + genre + ", homepage=" + homepage + ", alreadyInDvdtheque=" + alreadyInDvdtheque
				+ ", dateVue=" + dateVue+ ", dateSortieDvd=" + dateSortieDvd + "]";
	}
	@Override
	public int compareTo(Film film) {
		return this.getTitre().compareTo(film.getTitre());
	}
}
