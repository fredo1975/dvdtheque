package fr.fredos.dvdtheque.service;

import java.util.List;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public interface IPersonneService {
	public Personne findByPersonneId(Long id);
	public Personne findRealisateurByFilm(Film film);
	public List<Personne> findAllPersonne();
	public Long savePersonne(Personne personne);
	public void updatePersonne(Personne p);
	public void deletePersonne(Personne p);
	public Personne findPersonneByName(String nom);
	public Personne getPersonne(Long id);
	public Personne loadPersonne(Long id);
	public void cleanAllPersonnes();
	public List<Personne> findAllRealisateur();
	public List<Personne> findAllActeur();
	public Personne createOrRetrievePersonne(String nom);
	public Personne buildPersonne(String nom);
	public Long createPersonne(final String nom);
}
