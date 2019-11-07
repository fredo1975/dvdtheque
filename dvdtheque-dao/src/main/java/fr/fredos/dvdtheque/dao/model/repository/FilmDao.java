package fr.fredos.dvdtheque.dao.model.repository;

import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;

public interface FilmDao {
	Film findFilm(Long id);
	Genre findGenre(int id);
	Genre attachToSession(Genre genre);
	Genre saveGenre(Genre genre);
	Film findFilmByTitre(String titre);
	Film findFilmWithAllObjectGraph(Long id);
	Long saveNewFilm(Film film);
	void updateFilm(Film film);
	Long saveDvd(Dvd dvd);
	List<Film> findAllFilms();
	Set<Long> findAllTmdbFilms(Set<Long> tmdbIds);
	List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	void cleanAllFilms();
	List<Film> getAllRippedFilms();
	void removeFilm(Film film);
	Boolean checkIfTmdbFilmExists(Long tmdbId);
	void cleanAllGenres();
	public List<Genre> findAllGenres();
}
