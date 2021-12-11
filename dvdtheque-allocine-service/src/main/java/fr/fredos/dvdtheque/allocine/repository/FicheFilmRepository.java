package fr.fredos.dvdtheque.allocine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

@Repository
public interface FicheFilmRepository extends JpaRepository<FicheFilm, Integer>{

}
