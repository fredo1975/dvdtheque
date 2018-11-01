package fr.fredos.dvdtheque.service;

import java.util.List;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.dto.FilmDto;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
import fr.fredos.dvdtheque.service.dto.PersonnesFilm;
import fr.fredos.dvdtheque.service.dto.RealisateurDto;

public interface PersonneService {
	
	public PersonneDto findByPersonneId(Integer id);
	
	public RealisateurDto findRealisateurByFilm(FilmDto film);
	
	public List<PersonneDto> findAllPersonne();
	
	public PersonnesFilm findAllPersonneByFilm(FilmDto film);
	
	public PersonneDto savePersonne(PersonneDto personne);
	
	public PersonneDto updatePersonne(PersonneDto p);
	
	public void deletePersonne(PersonneDto p);
	
	public PersonneDto findPersonneByFullName(String nom,String prenom);
	
	public PersonneDto findPersonneByName(String nom);
	
	public Personne getPersonne(Integer id);
	
	public Personne loadPersonne(Integer id);
	
	public Personne savePersonne(Personne personne);
	
	public void cleanAllPersonnes();
	
	public List<PersonneDto> findAllRealisateur();
	
	public List<PersonneDto> findAllActeur();
}
