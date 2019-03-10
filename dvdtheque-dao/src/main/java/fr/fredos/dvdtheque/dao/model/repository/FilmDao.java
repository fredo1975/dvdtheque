package fr.fredos.dvdtheque.dao.model.repository;

import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;

public interface FilmDao {
	public Film findFilm(Long id);
	public Film findFilmByTitre(String titre);
	public Film findFilmWithAllObjectGraph(Long id);
	public Long saveNewFilm(Film film);
	public void updateFilm(Film film);
	public Long saveDvd(Dvd dvd);
	public List<Film> findAllFilms();
	public Set<Long> findAllTmdbFilms(Set<Long> tmdbIds);
	public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	public void cleanAllFilms();
	public List<Film> getAllRippedFilms();
	public void removeFilm(Film film);
}
