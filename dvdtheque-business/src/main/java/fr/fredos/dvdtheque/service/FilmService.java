package fr.fredos.dvdtheque.service;

import java.util.List;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.RippedFilm;
import fr.fredos.dvdtheque.dto.FilmDto;

public interface FilmService {
	
	public List<Film> getAllFilms();
	
	public FilmDto findFilm(Integer id);
	public RippedFilm findRippedFilm(Integer id);
	public RippedFilm findRippedFilmByTitre(String titre);
	public FilmDto saveNewFilm(FilmDto film);
	public void saveNewRippedFilm(RippedFilm film);
	
	//public DvdDto saveDvd(DvdDto dvdDto) throws Exception;
	
	public FilmDto findFilmWithAllObjectGraph(Integer id);
	public List<RippedFilm> findAllRippedFilms();
	public List<Film> findAllFilms();
	public Film updateFilm(Film film);
	public void saveNewFilm(Film film);
	public List<FilmDto> getAllFilmDtos();
	public void cleanAllFilms();
	public void cleanAllRippedFilms();
	public FilmDto findFilmByTitre(String titre);
	public List<Film> getAllRippedFilms();
	public List<FilmDto> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto);
	public void removeFilm(FilmDto film);
}
