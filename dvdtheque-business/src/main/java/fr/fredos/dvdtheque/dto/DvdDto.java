package fr.fredos.dvdtheque.dto;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import fr.fredos.dvdtheque.dao.model.object.Dvd;

public class DvdDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private java.lang.Integer id;
	// fields
	private Integer annee;
	private Integer zone;
	private String edition;
	public DvdDto() {
		super();
	}

	public Integer getZone() {
		return zone;
	}

	public void setZone(Integer zone) {
		this.zone = zone;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}
	
	@Override
	public String toString() {
		return "DvdDto [id=" + id + ", annee=" + annee + ", zone=" + zone + ", edition=" + edition + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DvdDto other = (DvdDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Dvd fromDto(DvdDto dvdDto){
		Dvd dvd = new Dvd();
		dvd.setZone(dvdDto.zone);
		dvd.setAnnee(dvdDto.annee);
		dvd.setEdition(dvdDto.edition);
		dvd.setId(dvdDto.id);
		return dvd;
		
	}
	public static DvdDto toDto(Dvd dvd){
		DvdDto dvdDto = new DvdDto();
		BeanUtils.copyProperties(dvd, dvdDto);
		/*
		if(null != dvd){
			dvdDto.setZone(dvd.getZone());
			dvdDto.setId(dvd.getId());
			dvdDto.setEdition(dvd.getEdition());
			dvdDto.setAnnee(dvd.getAnnee());
		}*/
		return dvdDto;
	}
	
}

