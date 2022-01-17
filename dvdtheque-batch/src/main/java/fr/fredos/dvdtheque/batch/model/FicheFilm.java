package fr.fredos.dvdtheque.batch.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FicheFilm {
	private int id;
	private String ficheFilm;
	private String url;
	private String allocineFilmId;
	private int pageNumber;
	private String title;
	private Set<CritiquePresse> critiquesPresse = new HashSet<>();
 
	public FicheFilm() {
		super();
	}
	public FicheFilm(String title,String ficheFilm, String url, String allocineFilmId, int pageNumber) {
		super();
		this.title = title;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Set<CritiquePresse> getCritiquesPresse() {
		return critiquesPresse;
	}
	public void setCritiquesPresse(Set<CritiquePresse> critiquesPresse) {
		this.critiquesPresse = critiquesPresse;
	}
	public void addCritiquePresse(CritiquePresse critiquePresse) {
		this.critiquesPresse.add(critiquePresse);
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
				+ allocineFilmId + ", pageNumber=" + pageNumber + ", title=" + title + ", critiquesPresse="
				+ critiquesPresse + "]";
	}
}
