package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

@Entity
@Table(name = "FILM")
public class Film implements Serializable {
	private static final long serialVersionUID = -1382161470818168805L;
	@Transient
	protected Logger logger = LoggerFactory.getLogger(Film.class);
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "ANNEE")
	private Integer annee;
	@Column(name = "TITRE")
	@NotNull
	private String titre;
	@Column(name = "TITRE_O")
	private String titreO;
	@JoinColumn(name = "ID_DVD")
	@ManyToOne(cascade=CascadeType.ALL)
	private Dvd dvd;
	@OneToMany(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	private Set<Personne> realisateurs = new HashSet<>();
	@OneToMany(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	private Set<Personne> acteurs = new HashSet<>();
	@Column(name = "RIPPED")
	private boolean ripped;
	@Column(name = "POSTER_PATH")
	private String posterPath;
	@Column(name = "TMDB_ID")
	private Long tmdbId;
	@Column(name = "OVERVIEW",length=500)
	private String overview;
	@Transient
	private boolean alreadyInDvdtheque;
	
	public Film() {
		super();
	}
	public Film(Long id) {
		super();
		this.id = id;
	}
	public Film(Long id, Integer annee, String titre, String titreO) {
		super();
		this.id = id;
		this.annee = annee;
		this.titre = titre;
		this.titreO = titreO;
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
	public boolean isRipped() {
		return ripped;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public void setRipped(boolean ripped) {
		this.ripped = ripped;
	}
	public String getPrintRealisateur() {
		if(!CollectionUtils.isEmpty(this.realisateurs)) {
			Personne realisateur = this.getRealisateurs().iterator().next();
			return realisateur.getNom();
		}
		return StringUtils.EMPTY;
	}
	public String getPrintActeurs() {
		StringBuilder sb = new StringBuilder();
		if(!CollectionUtils.isEmpty(this.getActeurs())) {
			for(Personne acteur : this.getActeurs()){
				sb.append(acteur.getNom()+" - ");
			}
		}
		return StringUtils.removeEnd(sb.toString(), " - ");
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
	@Override
	public String toString() {
		return "Film [id=" + id + ", annee=" + annee + ", titre=" + titre + ", titreO=" + titreO + ", dvd=" + dvd
				+ ", realisateurs=" + realisateurs + ", acteurs=" + acteurs + ", ripped=" + ripped + ", posterPath="
				+ posterPath + ", tmdbId=" + tmdbId + ", overview=" + overview + ", alreadyInDvdtheque="
				+ alreadyInDvdtheque + "]";
	}
}
