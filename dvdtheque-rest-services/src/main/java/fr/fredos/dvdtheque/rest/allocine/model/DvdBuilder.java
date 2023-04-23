package fr.fredos.dvdtheque.rest.allocine.model;

import fr.fredos.dvdtheque.rest.dao.domain.Film;

public class DvdBuilder {
	private Film filmToSave;
	private Integer zonedvd;
	private String filmFormat;
	
	public DvdBuilder() {
		super();
	}
	public Film getFilmToSave() {
		return filmToSave;
	}
	public void setFilmToSave(Film filmToSave) {
		this.filmToSave = filmToSave;
	}
	public Integer getZonedvd() {
		return zonedvd;
	}
	public void setZonedvd(Integer zonedvd) {
		this.zonedvd = zonedvd;
	}
	public String getFilmFormat() {
		return filmFormat;
	}
	public void setFilmFormat(String filmFormat) {
		this.filmFormat = filmFormat;
	}
	@Override
	public String toString() {
		return "DvdBuilder [filmToSave=" + filmToSave + ", zonedvd=" + zonedvd + ", filmFormat=" + filmFormat + "]";
	}
}
