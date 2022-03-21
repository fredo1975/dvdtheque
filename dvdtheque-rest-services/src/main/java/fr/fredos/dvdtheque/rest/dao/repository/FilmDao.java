package fr.fredos.dvdtheque.rest.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
@Repository("filmDao")
public interface FilmDao extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film>{
	List<Film> findFilmByTitre(final String titre);
	
	@Query("from Film where UPPER(REPLACE(REPLACE(titre, ':', ''),'  ',' ')) = UPPER(:titre)")
	Film findFilmByTitreWithoutSpecialsCharacters(final String titre);
	
	@Query("from Film film left join fetch film.dvd dvd where dvd.ripped=1")
	List<Film> getAllRippedFilms();
	
	@Query("select film.tmdbId from Film film where film.tmdbId in (:tmdbIds) ")
	Set<Long> findAllTmdbFilms(final Set<Long> tmdbIds);
	
	@Query("select count(1) from Film film where film.tmdbId = :tmdbId ")
	Boolean checkIfTmdbFilmExists(final Long tmdbId);
	
	
	List<Film> findFilmByOrigine(final FilmOrigine origine);
}
