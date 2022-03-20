package fr.fredos.dvdtheque.rest.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
@Repository("personneDao")
public interface PersonneDao extends JpaRepository<Personne, Long>{
	
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
