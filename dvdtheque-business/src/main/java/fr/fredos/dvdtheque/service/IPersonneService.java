package fr.fredos.dvdtheque.service;

import java.util.List;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public interface IPersonneService {
	public Personne findByPersonneId(Integer id);
	public Personne findRealisateurByFilm(Film film);
	public List<Personne> findAllPersonne();
	public Integer savePersonne(Personne personne);
	public void updatePersonne(Personne p);
	public void deletePersonne(Personne p);
	public Personne findPersonneByFullName(String nom);
	public Personne findPersonneByName(String nom);
	public Personne getPersonne(Integer id);
	public Personne loadPersonne(Integer id);
	public void cleanAllPersonnes();
	public List<Personne> findAllRealisateur();
	public List<Personne> findAllActeur();
	public Personne createOrRetrievePersonne(String nom);
	public Personne buildPersonne(String nom);
}
