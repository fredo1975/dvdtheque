package fr.fredos.dvdtheque.rest.service.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;

public class RealisateurDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private PersonneDto personne;
	public RealisateurDto() {
		super();
	}
	public RealisateurDto(PersonneDto personne) {
		this.setPersonne(personne);
	}
	public PersonneDto getPersonne() {
		return personne;
	}
	public void setPersonne(PersonneDto personne) {
		this.personne = personne;
	}
	public static RealisateurDto toDto(Personne realisateur){
		RealisateurDto realisateurDto = new RealisateurDto();
		realisateurDto.setPersonne(PersonneDto.toDto(realisateur));
		return realisateurDto;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((personne == null) ? 0 : personne.hashCode());
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
		RealisateurDto other = (RealisateurDto) obj;
		if (personne == null) {
			if (other.personne != null)
				return false;
		} else if (!personne.equals(other.personne))
			return false;
		return true;
	}

	@Override
    public String toString(){
		super.toString();
		return new ToStringBuilder(this).
	       append("personne", personne).
	       toString();
    }
	
	public static Personne fromDto(RealisateurDto realisateurDto,Film film){
		Personne realisateur = PersonneDto.fromDto(realisateurDto.getPersonne());
		return realisateur;
	}
}
