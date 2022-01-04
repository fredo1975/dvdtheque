package fr.fredos.dvdtheque.allocine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

@Repository
public interface FicheFilmRepository extends JpaRepository<FicheFilm, Integer>{
	@Query("from FicheFilm f where f.title = :title")
	FicheFilm findByTitle(@Param("title") String title);
	
	@Query("from FicheFilm f where f.allocineFilmId = :ficheFilm")
	FicheFilm findByFicheFilmId(@Param("ficheFilm") Integer ficheFilm);
	
	@Query("from FicheFilm f join fetch f.critiquePresse as cp where cp is empty")
	List<FicheFilm> findAllFilmWithoutCritique();
}
