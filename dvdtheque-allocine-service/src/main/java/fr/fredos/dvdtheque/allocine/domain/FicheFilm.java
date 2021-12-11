package fr.fredos.dvdtheque.allocine.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FicheFilm {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
	@Column(name="ficheFilm")
	private String ficheFilm;
	@Column(name="url")
	private String url;
	@Column(name="allocineFilmId")
	private String allocineFilmId;
	@Column(name="pageNumber")
	private int pageNumber;
	
	public FicheFilm() {
		super();
	}
	public FicheFilm(String ficheFilm, String url, String allocineFilmId, int pageNumber) {
		super();
		this.ficheFilm = ficheFilm;
		this.url = url;
		this.allocineFilmId = allocineFilmId;
		this.pageNumber = pageNumber;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFicheFilm() {
		return ficheFilm;
	}
	public void setFicheFilm(String ficheFilm) {
		this.ficheFilm = ficheFilm;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAllocineFilmId() {
		return allocineFilmId;
	}
	public void setAllocineFilmId(String allocineFilmId) {
		this.allocineFilmId = allocineFilmId;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	@Override
	public int hashCode() {
		return Objects.hash(allocineFilmId);
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
		return Objects.equals(allocineFilmId, other.allocineFilmId);
	}
	@Override
	public String toString() {
		return "FicheFilm [id=" + id + ", ficheFilm=" + ficheFilm + ", url=" + url + ", allocineFilmId="
				+ allocineFilmId + ", pageNumber=" + pageNumber + "]";
	}
	
}
