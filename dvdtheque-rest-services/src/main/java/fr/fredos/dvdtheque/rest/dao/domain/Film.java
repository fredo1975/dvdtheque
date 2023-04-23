package fr.fredos.dvdtheque.rest.dao.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.rest.service.model.CritiquePresse;

@Entity
@Table(name = "film")
public class Film implements Serializable, Comparable<Film> {
	private static final long serialVersionUID = -1382161470818168805L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "annee")
	private Integer annee;
	@Column(name = "date_sortie")
	@Temporal(TemporalType.DATE)
	private Date dateSortie;
	@Column(name = "date_insertion")
	@Temporal(TemporalType.DATE)
	private Date dateInsertion;
	@Column(name = "date_sortie_dvd")
	@Temporal(TemporalType.DATE)
	private Date dateSortieDvd;
	@Column(name = "titre")
	@NotNull
	private String titre;
	@Column(name = "titre_o")
	private String titreO;
	@JoinColumn(name = "dvd_id",nullable = true)
	@OneToOne(cascade=CascadeType.ALL)
	private Dvd dvd;
	@Column(name = "origine")
	private FilmOrigine origine;
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	private Set<Personne> realisateur = new HashSet<>();
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	private Set<Personne> acteur = new HashSet<>();
	@Column(name = "vu")
	private boolean vu;
	@Column(name = "poster_path")
	private String posterPath;
	@Column(name = "tmdb_id")
	private Long tmdbId;
	@Column(name = "overview",length=65535)
	private String overview;
	@Column(name = "runtime")
	private Integer runtime;
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	private Set<Genre> genre = new HashSet<>();
	@Column(name = "homepage")
	private String homepage;
	@Transient
	private boolean alreadyInDvdtheque;
	@Column(name="update_ts", insertable=false, updatable=true)
	private LocalDateTime dateMaj;
	@Column(name="vue_date", insertable=true, updatable=true)
	private LocalDate dateVue;
	@Transient
	private List<CritiquePresse> critiquePresse;
	@Column(name = "allocine_fiche_film_id")
	private Integer allocineFicheFilmId;
	
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
	public Set<Personne> getActeur() {
		return acteur;
	}
	public void setActeur(Set<Personne> acteur) {
		this.acteur = acteur;
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
	
	public Set<Genre> getGenre() {
		return genre;
	}
	public void setGenre(Set<Genre> genre) {
		this.genre = genre;
	}
	public LocalDateTime getDateMaj() {
		return dateMaj;
	}
	public void setDateMaj(LocalDateTime dateMaj) {
		this.dateMaj = dateMaj;
	}
	public List<CritiquePresse> getCritiquePresse() {
		return critiquePresse;
	}
	public void setCritiquePresse(List<CritiquePresse> critiquePresse) {
		this.critiquePresse = critiquePresse;
	}
	public LocalDate getDateVue() {
		return dateVue;
	}
	public void setDateVue(LocalDate dateVue) {
		this.dateVue = dateVue;
	}
	public void addCritiquePresse(CritiquePresse critiquePresse) {
		if(CollectionUtils.isEmpty(this.critiquePresse)) {
			this.critiquePresse = new ArrayList<>();
		}
		this.critiquePresse.add(critiquePresse);
	}
	
	public Integer getAllocineFicheFilmId() {
		return allocineFicheFilmId;
	}
	public void setAllocineFicheFilmId(Integer allocineFicheFilmId) {
		this.allocineFicheFilmId = allocineFicheFilmId;
	}
	@Override
	public int compareTo(Film film) {
		return this.getTitre().compareTo(film.getTitre());
	}
	@Override
	public String toString() {
		return "Film [id=" + id + ", annee=" + annee + ", dateSortie=" + dateSortie + ", dateInsertion=" + dateInsertion
				+ ", titre=" + titre + ", titreO=" + titreO + ", dvd=" + dvd + ", origine=" + origine + ", realisateur="
				+ realisateur + ", acteur=" + acteur + ", vu=" + vu + ", posterPath=" + posterPath + ", tmdbId="
				+ tmdbId + ", overview=" + overview + ", runtime=" + runtime + ", genre=" + genre + ", homepage="
				+ homepage + ", alreadyInDvdtheque=" + alreadyInDvdtheque + ", dateMaj=" + dateMaj + ", dateVue="
				+ dateVue + ", critiquePresse=" + critiquePresse + ", allocineFicheFilmId=" + allocineFicheFilmId 
				+ ", dateSortieDvd=" + dateSortieDvd+ "]";
	}
}
