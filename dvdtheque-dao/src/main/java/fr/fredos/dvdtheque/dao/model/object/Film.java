package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.common.enums.FilmOrigine;

@Entity
@Table(name = "FILM")
public class Film implements Serializable, Comparable<Film> {
	private static final long serialVersionUID = -1382161470818168805L;
	@Transient
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "ANNEE")
	private Integer annee;
	@Column(name = "DATE_SORTIE")
	@Temporal(TemporalType.DATE)
	private Date dateSortie;
	@Column(name = "DATE_INSERTION")
	@Temporal(TemporalType.DATE)
	private Date dateInsertion;
	@Column(name = "TITRE")
	@NotNull
	private String titre;
	@Column(name = "TITRE_O")
	private String titreO;
	@JoinColumn(name = "ID_DVD",nullable = true)
	@ManyToOne(cascade=CascadeType.ALL)
	private Dvd dvd;
	@Column(name = "ORIGINE")
	private FilmOrigine origine;
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	private Set<Personne> realisateurs = new HashSet<>();
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	private Set<Personne> acteurs = new HashSet<>();
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	@OrderBy("note desc")
	private Set<CritiquesPresse> critiquesPresse = new TreeSet<>();
	@Column(name = "VU")
	private boolean vu;
	@Column(name = "POSTER_PATH")
	private String posterPath;
	@Column(name = "TMDB_ID")
	private Long tmdbId;
	@Column(name = "OVERVIEW",length=65535)
	private String overview;
	@Column(name = "RUNTIME")
	private Integer runtime;
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	private Set<Genre> genres = new HashSet<>();
	@Column(name = "HOMEPAGE")
	private String homepage;
	@Transient
	private boolean alreadyInDvdtheque;
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
	public Set<Personne> getRealisateurs() {
		return realisateurs;
	}
	public void setRealisateurs(Set<Personne> realisateurs) {
		this.realisateurs = realisateurs;
	}
	public Set<Personne> getActeurs() {
		return acteurs;
	}
	public void setActeurs(Set<Personne> acteurs) {
		this.acteurs = acteurs;
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
	public Set<Genre> getGenres() {
		return genres;
	}
	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}
	@Override
	public String toString() {
		return "Film [id=" + id + ", annee=" + annee + ", dateSortie=" + dateSortie + ", dateInsertion=" + dateInsertion
				+ ", titre=" + titre + ", titreO=" + titreO + ", dvd=" + dvd + ", origine=" + origine
				+ ", realisateurs=" + realisateurs + ", acteurs=" + acteurs + ", critiquesPresse=" + critiquesPresse
				+ ", vu=" + vu + ", posterPath=" + posterPath + ", tmdbId=" + tmdbId + ", overview=" + overview
				+ ", runtime=" + runtime + ", genres=" + genres + ", homepage=" + homepage + ", alreadyInDvdtheque="
				+ alreadyInDvdtheque + "]";
	}
	@Override
	public int compareTo(Film film) {
		return this.getTitre().compareTo(film.getTitre());
	}
}
