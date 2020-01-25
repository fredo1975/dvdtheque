package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Entity
@Table(name = "CRITIQUES_PRESSE")
public class CritiquesPresse implements Serializable{
	private static final long serialVersionUID = 1L;
	@Transient
	private Log logger = LogFactory.getLog(this.getClass());
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "CODE")
	private Integer code;
	@Column(name = "NOM_SOURCE")
	private String nomSource;
	@Column(name = "AUTEUR")
	private String auteur;
	@Column(name = "CRITIQUE")
	private String critique;
	@Column(name = "NOTE")
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
}
