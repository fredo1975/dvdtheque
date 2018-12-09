package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import fr.fredos.dvdtheque.common.dto.NewActeurDto;
@Entity
@Table(name = "PERSONNE")
public class Personne implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private java.lang.Integer id;
	@Column(name = "NOM")
	@Size(min = 1, max = 45)
	private String nom;
	@Column(name = "PRENOM")
	@Size(min = 1, max = 45)
	private String prenom;
	@Column(name = "DATE_N")
	private Date dateN;
	@JoinColumn(name = "ID_PAYS")
	@ManyToOne
	private Pays pays;
	public Personne() {
		super();
	}
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public Date getDateN() {
		return dateN;
	}
	public void setDateN(Date dateN) {
		this.dateN = dateN;
	}
	public Pays getPays() {
		return pays;
	}
	public void setPays(Pays pays) {
		this.pays = pays;
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
		Personne other = (Personne) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return prenom+" "+nom;
	}
	
	public static final Personne buildPersonneFromNewActeurDto(NewActeurDto newActeurDto) {
		Personne p = new Personne();
		p.setNom(newActeurDto.getNom());
		p.setPrenom(newActeurDto.getPrenom());
		return p;
	}
}
