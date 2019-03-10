package fr.fredos.dvdtheque.dao.model.repository;

import java.util.List;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public interface PersonneDao {
	
	public Personne findByPersonneId(Long id);
	public Personne getPersonne(Long id);
	public Personne loadPersonne(Long id);
	public Personne findRealisateurByFilm(Film film);
	public List<Personne> findAllRealisateur();
	public List<Personne> findAllActeur();
    public List<Personne> findAllPersonne();
	public List<Personne> findAllPersonneByType(Integer typeId);
	public Personne findPersonneByName(String nom);
    public List<Personne> findAllPersonneByFilm(Film film);
	public Long savePersonne(Personne p);
	public void updatePersonne(Personne p);
	public void deletePersonne(Personne p);
	public void cleanAllPersons();
}
