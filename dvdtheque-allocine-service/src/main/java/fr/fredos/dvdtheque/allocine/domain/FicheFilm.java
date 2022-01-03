package fr.fredos.dvdtheque.allocine.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "fiche_film")
public class FicheFilm {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	@Column(name="fiche_film")
	private int ficheFilm;
	@Column(name="url")
	private String url;
	@Column(name="page_number")
	private int pageNumber;
	@Column(name="title")
	private String title;
	@OneToMany(cascade=CascadeType.PERSIST,fetch = FetchType.EAGER)
	private Set<CritiquePresse> critiques = new HashSet<>();
 
	public FicheFilm() {
		super();
	}
	public FicheFilm(String title,int ficheFilm, String url,int pageNumber) {
		super();
		this.title = title;
		this.ficheFilm = ficheFilm;
		this.url = url;
		this.pageNumber = pageNumber;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFicheFilm() {
		return ficheFilm;
	}
	public void setFicheFilm(int ficheFilm) {
		this.ficheFilm = ficheFilm;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Set<CritiquePresse> getCritiques() {
		return critiques;
	}
	public void setCritiques(Set<CritiquePresse> critiques) {
		this.critiques = critiques;
	}
	public void addCritiques(CritiquePresse critique) {
		this.critiques.add(critique);
	}
	@Override
	public int hashCode() {
		return Objects.hash(ficheFilm);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FicheFilm other = (FicheFilm) obj;
		return Objects.equals(ficheFilm, other.ficheFilm);
	}
	@Override
	public String toString() {
		return "FicheFilm [id=" + id + ", ficheFilm=" + ficheFilm + ", url=" + url + ", pageNumber=" + pageNumber + ", title=" + title + ", critiques="
				+ critiques + "]";
	}
}
