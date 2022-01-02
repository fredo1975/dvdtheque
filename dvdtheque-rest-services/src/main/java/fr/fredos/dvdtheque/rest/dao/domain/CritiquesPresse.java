package fr.fredos.dvdtheque.rest.dao.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "critiques_presse")
public class CritiquesPresse implements Serializable,Comparable<CritiquesPresse>{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "code")
	private Integer code;
	@Column(name = "nom_source")
	private String nomSource;
	@Column(name = "auteur")
	private String auteur;
	@Column(name = "critique",length=65535)
	private String critique;
	@Column(name = "note")
	private Double note;
	
	public CritiquesPresse() {
		super();
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
		CritiquesPresse other = (CritiquesPresse) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getNomSource() {
		return nomSource;
	}
	public void setNomSource(String nomSource) {
		this.nomSource = nomSource;
	}
	public String getAuteur() {
		return auteur;
	}
	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}
	public String getCritique() {
		return critique;
	}
	public void setCritique(String critique) {
		this.critique = critique;
	}
	public Double getNote() {
		return note;
	}
	public void setNote(Double note) {
		this.note = note;
	}
	@Override
	public String toString() {
		return "CritiquesPresse [id=" + id + ", code=" + code + ", nomSource=" + nomSource + ", auteur="
				+ auteur + ", critique=" + critique + ", note=" + note + "]";
	}
	@Override
	public int compareTo(CritiquesPresse o) {
		if(o.getNote().compareTo(this.getNote())!=0) {
			return o.getNote().compareTo(this.getNote());
		} else {
			return this.getNomSource().compareTo(o.nomSource);
		}
	}
}
