package fr.fredos.dvdtheque.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class ActeurDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private PersonneDto personne;
	
	public ActeurDto() {
		super();
	}
	
	public PersonneDto getPersonne() {
		return personne;
	}
	public void setPersonne(PersonneDto personne) {
		this.personne = personne;
	}
	public static ActeurDto toDto(Personne acteur){
		ActeurDto acteurDto = new ActeurDto();
		acteurDto.setPersonne(PersonneDto.toDto(acteur));
		return acteurDto;
	}
	public static Personne fromDto(ActeurDto acteurDto,Film film){
		Personne acteur = PersonneDto.fromDto(acteurDto.getPersonne());
		return acteur;
	}
	/*
	public Acteur fromDto(PersonneDto personne){
		this.personne = personne;
	}*/
	@Override
    public String toString(){
		return new ToStringBuilder(this).
	       append("personne", personne).toString();
    }
}
