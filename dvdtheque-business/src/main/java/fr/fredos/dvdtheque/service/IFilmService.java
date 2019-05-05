package fr.fredos.dvdtheque.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;

public interface IFilmService {
	public static final String CACHE_DIST_FILM = "dist-film";
	Date clearDate(Date dateToClear);
	Film findFilm(Long id);
	Film findFilmWithAllObjectGraph(Long id);
	List<Film> findAllFilms();
	Set<Long> findAllTmdbFilms(Set<Long> tmdbIds);
	void updateFilm(Film film);
	Long saveNewFilm(Film film);
	//public List<FilmDto> getAllFilmDtos();
	void cleanAllFilms();
	Film findFilmByTitre(String titre);
	List<Film> getAllRippedFilms();
	List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	void removeFilm(Film film);
	Film createOrRetrieveFilm(final String titre,final Integer annee,
			final String realNom,
			final String act1Nom,
			final String act2Nom,
			final String act3Nom, Date ripDate);
	Dvd buildDvd(final Integer annee,final Integer zone,final String edition, Date ripDate);
	Boolean checkIfTmdbFilmExists(Long tmdbId);
}
