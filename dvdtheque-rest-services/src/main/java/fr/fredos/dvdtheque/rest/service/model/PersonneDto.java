package fr.fredos.dvdtheque.rest.service.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;

import fr.fredos.dvdtheque.rest.dao.domain.Pays;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;

public class PersonneDto implements Serializable,Comparable<PersonneDto> {

	private static final long serialVersionUID = 1L;
	
	public PersonneDto() {
		super();
	}
	
	public PersonneDto(String nom, String prenom) {
		super();
		this.nom = nom;
		this.prenom = prenom;
	}

	public PersonneDto(Long id, String nom, String prenom, Date dateN,
			Pays pays) {
		super();
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.dateN = dateN;
		this.pays = pays;
	}

	private Long id;
	// fields
	private String nom;
	private String prenom;
	private Date dateN;
	private Pays pays;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
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
		PersonneDto other = (PersonneDto) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prenom == null) {
			if (other.prenom != null)
				return false;
		} else if (!prenom.equals(other.prenom))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("nom", nom)
				.append("prenom", prenom).append("dateN", dateN).append("pays", pays).toString();
	}
	public static PersonneDto toDto(Personne personne){
		PersonneDto personneDto = new PersonneDto();
		BeanUtils.copyProperties(personne, personneDto);
		return personneDto;
	}
	public static Personne fromDto(PersonneDto personneDto){
		Personne personne = new Personne();
		BeanUtils.copyProperties(personneDto, personne);
		return personne;
	}

	@Override
	public int compareTo(PersonneDto o) {
		if ((this.getPrenom()+" "+this.getNom()).compareTo(o.getPrenom()+" "+o.getNom())<0){
            return -1;
        }else{
            return 1;
        }
	}
	
}
