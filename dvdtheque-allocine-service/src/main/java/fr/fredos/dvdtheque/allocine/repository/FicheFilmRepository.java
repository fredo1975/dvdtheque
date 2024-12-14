package fr.fredos.dvdtheque.allocine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

@Repository
public interface FicheFilmRepository extends JpaRepository<FicheFilm, Integer>, JpaSpecificationExecutor<FicheFilm>{
	@Query("select f from FicheFilm f join fetch f.critiquePresse where upper(f.title) = upper(:title)")
	List<FicheFilm> findByTitle(@Param("title") String title);
	
	@Query("select f from FicheFilm f where f.allocineFilmId = :ficheFilm")
	FicheFilm findByFicheFilmId(@Param("ficheFilm") Integer ficheFilm);
}
