package fr.fredos.dvdtheque.batch.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dvd implements Serializable {
	private static final long serialVersionUID = 1L;
	private Log logger = LogFactory.getLog(this.getClass());
	private Long id;
	private Integer annee;
	private Date dateSortie;
	private Integer zone;
	private String edition;
	private Date dateRip;
	private DvdFormat format;
	private boolean ripped;
	public Long getId() {
		return id;
	}
	public void setId(Long _id) {
		this.id = _id;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer _annee) {
		this.annee = _annee;
	}
	public Integer getZone() {
		return zone;
	}
	public void setZone(Integer zone) {
		this.zone = zone;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String _edition) {
		this.edition = _edition;
	}
	public Date getDateRip() {
		return dateRip;
	}
	public void setDateRip(Date dateRip) {
		this.dateRip = dateRip;
	}
	public DvdFormat getFormat() {
		return format;
	}
	public void setFormat(DvdFormat format) {
		this.format = format;
	}
	public boolean isRipped() {
		return ripped;
	}
	public void setRipped(boolean ripped) {
		this.ripped = ripped;
	}
	public Date getDateSortie() {
		return dateSortie;
	}
	public void setDateSortie(Date dateSortie) {
		this.dateSortie = dateSortie;
	}
	public Dvd() {
		super();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dvd other = (Dvd) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public String toString() {
		return "Dvd [id=" + id + ", annee=" + annee + ", dateSortie=" + dateSortie + ", zone=" + zone + ", edition="
				+ edition + ", dateRip=" + dateRip + ", format=" + format + ", ripped=" + ripped + "]";
	}
}
