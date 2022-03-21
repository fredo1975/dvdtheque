package fr.fredos.dvdtheque.rest.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.rest.dao.domain.Genre;

@Repository("genreDao")
public interface GenreDao extends JpaRepository<Genre, Long>{
	Genre findGenreByTmdbId(int tmdbId);
}
