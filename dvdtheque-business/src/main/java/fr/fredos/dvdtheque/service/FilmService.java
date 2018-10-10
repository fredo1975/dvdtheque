package fr.fredos.dvdtheque.service;

import java.util.List;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.dto.FilmDto;

public interface FilmService {
	
	public List<Film> getAllFilms();
	
	public FilmDto findFilm(Integer id);
	
	public FilmDto saveNewFilm(FilmDto film);
	
	//public DvdDto saveDvd(DvdDto dvdDto) throws Exception;
	
	public FilmDto findFilmWithAllObjectGraph(Integer id);
	
	public List<Film> findAllFilms();
	public Film updateFilm(Film film);
	public void saveNewFilm(Film film);
	public List<FilmDto> getAllFilmDtos();
	public void cleanAllFilms();
	public FilmDto findFilmByTitre(String titre);
	public List<Film> getAllRippedFilms();
	public List<FilmDto> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	public void removeFilm(FilmDto film);
}
