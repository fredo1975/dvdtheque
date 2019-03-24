package fr.fredos.dvdtheque.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;

public interface IFilmService {
	public static final String CACHE_DIST_FILM = "dist-film";
	public Date clearDate(Date dateToClear);
	public Film findFilm(Long id);
	public Film findFilmWithAllObjectGraph(Long id);
	public List<Film> findAllFilms();
	public Set<Long> findAllTmdbFilms(Set<Long> tmdbIds);
	public void updateFilm(Film film);
	public Long saveNewFilm(Film film);
	//public List<FilmDto> getAllFilmDtos();
	public void cleanAllFilms();
	public Film findFilmByTitre(String titre);
	public List<Film> getAllRippedFilms();
	public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	public void removeFilm(Film film);
	public Film createOrRetrieveFilm(final String titre,final Integer annee,
			final String realNom,
			final String act1Nom,
			final String act2Nom,
			final String act3Nom, Date ripDate);
	public Dvd buildDvd(final Integer annee,final Integer zone,final String edition, Date ripDate);
}
