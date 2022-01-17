package fr.fredos.dvdtheque.rest.service.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import fr.fredos.dvdtheque.rest.dao.domain.Personne;

public class PersonnesFilm implements Serializable {

	private static final long serialVersionUID = 1L;

	private RealisateurDto realisateur;
	private Set<ActeurDto> acteurs;
	public PersonnesFilm() {
		super();
		acteurs = new HashSet<ActeurDto>();
	}
	public RealisateurDto getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(RealisateurDto realisateur) {
		this.realisateur = realisateur;
	}
	public Set<ActeurDto> getActeurs() {
		return acteurs;
	}
	public void setActeur(Set<ActeurDto> acteurs) {
		this.acteurs = acteurs;
	}
	
	public void addActeur(Set<Personne> a){
		for(Personne p : a){
			ActeurDto acteurDto = ActeurDto.toDto(p);
			if(CollectionUtils.isEmpty(this.acteurs)){
				this.acteurs = new HashSet<>(a.size());
			}
			this.acteurs.add(acteurDto);
		}
	}
	@Override
	public String toString() {
		return "PersonnesFilm [realisateur=" + realisateur + ", acteurs=" + acteurs + "]";
	}
}
