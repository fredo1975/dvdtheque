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
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "FILM")
public class Film implements Serializable {
	private static final long serialVersionUID = -1382161470818168805L;
	@Transient
	protected Logger logger = LoggerFactory.getLogger(Film.class);
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private java.lang.Integer id;
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
	//@OneToMany(cascade=CascadeType.ALL,fetch = FetchType.LAZY,mappedBy ="realisateurFilm.film")
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.LAZY)
    @JoinTable(name = "REALISATEUR", joinColumns = @JoinColumn(name = "ID_FILM"),
        inverseJoinColumns = @JoinColumn(name = "ID_PERSONNE"))
	private Set<Personne> realisateurs = new HashSet<Personne>();
	//@OneToMany(cascade=CascadeType.ALL,fetch = FetchType.LAZY,mappedBy ="acteurFilm.film")
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.LAZY)
    @JoinTable(name = "ACTEUR", joinColumns = @JoinColumn(name = "ID_FILM"),
        inverseJoinColumns = @JoinColumn(name = "ID_PERSONNE"))
	private Set<Personne> acteurs = new HashSet<Personne>();
	@Column(name = "RIPPED")
	private boolean ripped;
	@Transient
	private int hashCode = Integer.MIN_VALUE;

	public Film() {
		super();
	}

	public Film(Integer id) {
		super();
		this.id = id;
	}

	public Film(Integer id, Integer annee, String titre, String titreO) {
		super();
		this.id = id;
		this.annee = annee;
		this.titre = titre;
		this.titreO = titreO;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
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
	
	public Set<Personne> getActeurs() {
		return acteurs;
	}

	public void setActeurs(Set<Personne> acteurs) {
		this.acteurs = acteurs;
	}

	public Set<Personne> getRealisateurs() {
		return realisateurs;
	}

	public void setRealisateurs(Set<Personne> realisateurs) {
		this.realisateurs = realisateurs;
	}
	public boolean isRipped() {
		return ripped;
	}

	public void setRipped(boolean ripped) {
		this.ripped = ripped;
	}

	@Override
	public String toString() {
		return "Film [id=" + id + ", annee=" + annee + ", titre=" + titre + ", titreO=" + titreO + ", dvd=" + dvd
				+ ", realisateurs=" + realisateurs + ", acteurs=" + acteurs + ", ripped=" + ripped + "]";
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

}
