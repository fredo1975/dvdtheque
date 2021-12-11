package fr.fredos.dvdtheque.allocine.scraping.model;

import java.util.Objects;

public class FicheFilm {
	private final String ficheFilm;
	private final String url;
	private final String filmId;
	private final int pageNumber;
	public FicheFilm(String ficheFilm, String url,String filmId,int pageNumber) {
		super();
		this.ficheFilm = ficheFilm;
		this.url = url;
		this.filmId = filmId;
		this.pageNumber = pageNumber;
	}
	public String getFicheFilm() {
		return ficheFilm;
	}
	public String getUrl() {
		return url;
	}
	
	public String getFilmId() {
		return filmId;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	@Override
	public int hashCode() {
		return Objects.hash(filmId);
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
		return Objects.equals(filmId, other.filmId);
	}
	@Override
	public String toString() {
		return "FicheFilm [ficheFilm=" + ficheFilm + ", url=" + url + ", filmId=" + filmId + ", pageNumber="
				+ pageNumber + "]";
	}
	
}
