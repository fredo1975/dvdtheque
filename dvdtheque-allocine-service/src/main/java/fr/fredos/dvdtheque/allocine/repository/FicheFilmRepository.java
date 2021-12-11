package fr.fredos.dvdtheque.allocine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

@Repository
public interface FicheFilmRepository extends JpaRepository<FicheFilm, Integer>{
	@Query("from FicheFilm f where f.title = :title")
	FicheFilm findByTitle(@Param("title") String title);
}
