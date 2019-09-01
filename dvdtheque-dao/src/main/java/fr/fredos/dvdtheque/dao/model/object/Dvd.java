package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.common.enums.DvdFormat;

@Entity
@Table(name = "DVD")
public class Dvd implements Serializable {
	private static final long serialVersionUID = 1L;
	@Transient
	private Log logger = LogFactory.getLog(this.getClass());
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "ANNEE")
	private Integer annee;
	@Column(name = "ZONE")
	private Integer zone;
	@Column(name = "EDITION")
	private String edition;
	@Column(name = "DATE_RIP")
	@Temporal(TemporalType.DATE)
	private Date dateRip;
	@Column(name = "FORMAT")
	private DvdFormat format;
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
		return "Dvd [id=" + id + ", annee=" + annee + ", zone=" + zone + ", edition=" + edition
				+ ", dateRip=" + dateRip + ", format=" + format + "]";
	}
}
