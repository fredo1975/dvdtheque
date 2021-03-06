package fr.fredos.dvdtheque.dao.model.repository;

import java.util.List;

import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public interface PersonneDao {
	
	Personne findByPersonneId(Long id);
	Personne getPersonne(Long id);
	Personne loadPersonne(Long id);
	Personne findRealisateurByFilm(Film film);
	List<Personne> findAllRealisateur();
	List<Personne> findAllRealisateursByOrigine(FilmOrigine filmOrigine);
	List<Personne> findAllActeur();
	List<Personne> findAllActeursByOrigine(FilmOrigine filmOrigine);
    List<Personne> findAllPersonne();
	List<Personne> findAllPersonneByType(Integer typeId);
	Personne findPersonneByName(String nom);
    List<Personne> findAllPersonneByFilm(Film film);
	Long savePersonne(Personne p);
	void updatePersonne(Personne p);
	void deletePersonne(Personne p);
	void cleanAllPersons();
}
