package fr.fredos.dvdtheque.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public interface IFilmService {
	public static final String CACHE_DIST_FILM = "dist-film";
	Date clearDate(Date dateToClear);
	Film findFilm(Long id);
	Film findFilmWithAllObjectGraph(final Long id);
	List<Film> findAllFilms();
	Set<Long> findAllTmdbFilms(Set<Long> tmdbIds);
	void updateFilm(Film film);
	Long saveNewFilm(Film film);
	//public List<FilmDto> getAllFilmDtos();
	void cleanAllFilms();
	Film findFilmByTitre(final String titre);
	List<Film> getAllRippedFilms();
	List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	void removeFilm(Film film);
	Dvd buildDvd(final Integer annee,final Integer zone,final String edition,final Date ripDate,final DvdFormat dvdFormat);
	Boolean checkIfTmdbFilmExists(final Long tmdbId);
	Genre findGenre(int id);
	Genre attachToSession(Genre genre);
	Genre saveGenre(Genre genre);
	List<Genre> findAllGenres();
	List<Film> findAllFilmsByOrigine(FilmOrigine filmOrigine);
	void cleanAllCaches();
	List<Personne> findAllRealisateurs();
	List<Personne> findAllActeurs();
	List<Personne> findAllActeursByOrigine(FilmOrigine filmOrigine);
	List<Personne> findAllRealisateursByOrigine(FilmOrigine filmOrigine);
}
