package fr.fredos.dvdtheque.batch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.fredos.dvdtheque.dao.model.object.Film;
@JsonIgnoreProperties(ignoreUnknown = true)
public class DvdBuilder {
	private Film filmToSave;
	private Integer zonedvd;
	private String filmFormat;
	private String dateSortieDvd;
	
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
	public String getDateSortieDvd() {
		return dateSortieDvd;
	}
	public void setDateSortieDvd(String dateSortieDvd) {
		this.dateSortieDvd = dateSortieDvd;
	}
	@Override
	public String toString() {
		return "DvdBuilder [filmToSave=" + filmToSave + ", zonedvd=" + zonedvd + ", filmFormat=" + filmFormat
				+ ", dateSortieDvd=" + dateSortieDvd + "]";
	}
	
}
