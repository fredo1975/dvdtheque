package fr.fredos.dvdtheque.allocine.dto;

import java.util.HashSet;
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
	public String toString() {
		return "FicheFilmDto [id=" + id + ", allocineFilmId=" + allocineFilmId + ", url=" + url + ", pageNumber="
				+ pageNumber + ", title=" + title + ", critiquePresse=" + critiquePresse + "]";
	}
	
	
}
