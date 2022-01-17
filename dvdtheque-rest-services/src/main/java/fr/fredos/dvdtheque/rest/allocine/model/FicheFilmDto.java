package fr.fredos.dvdtheque.rest.allocine.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FicheFilmDto {
	private int id;
	private int allocineFilmId;
	private String url;
	private int pageNumber;
	private String title;
	private Set<CritiquePresseDto> critiquePresse = new HashSet<>();
	public FicheFilmDto() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAllocineFilmId() {
		return allocineFilmId;
	}
	public void setAllocineFilmId(int allocineFilmId) {
		this.allocineFilmId = allocineFilmId;
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
	public Set<CritiquePresseDto> getCritiquePresse() {
		return critiquePresse;
	}
	public void setCritiquePresse(Set<CritiquePresseDto> critiquePresse) {
		this.critiquePresse = critiquePresse;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FicheFilmDto other = (FicheFilmDto) obj;
		return id == other.id;
	}
	@Override
	public String toString() {
		return "FicheFilmDto [id=" + id + ", allocineFilmId=" + allocineFilmId + ", url=" + url + ", pageNumber="
				+ pageNumber + ", title=" + title + ", critiquePresse=" + critiquePresse + "]";
	}
	
}
