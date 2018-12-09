package fr.fredos.dvdtheque.service;

import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.dto.NewActeurDto;
import fr.fredos.dvdtheque.dao.model.object.Film;

public interface FilmService {
	public Film findFilm(Integer id);
	public Film findFilmWithAllObjectGraph(Integer id);
	public List<Film> findAllFilms();
	public void updateFilm(Film film);
	public void updateFilmWithNewPersons(Film film,Set<NewActeurDto> newActeurDtoSet);
	public Integer saveNewFilm(Film film);
	public Integer saveNewFilmWithNewPersons(Film film,Set<NewActeurDto> newActeurDtoSet);
	//public List<FilmDto> getAllFilmDtos();
	public void cleanAllFilms();
	public Film findFilmByTitre(String titre);
	public List<Film> getAllRippedFilms();
	public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	public void removeFilm(Film film);
}
