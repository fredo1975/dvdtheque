package fr.fredos.dvdtheque.rest.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.rest.dao.domain.Dvd;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import fr.fredos.dvdtheque.rest.service.model.FilmListParam;

public interface IFilmService {
	public static final String CACHE_DIST_FILM = "dist-film";
	Date clearDate(final Date dateToClear);
	Film findFilm(Long id);
	List<Film> findAllFilms(FilmDisplayTypeParam filmDisplayTypeParam);
	Set<Long> findAllTmdbFilms(final Set<Long> tmdbIds);
	Film updateFilm(Film film);
	Long saveNewFilm(Film film);
	//public List<FilmDto> getAllFilmDtos();
	void cleanAllFilms();
	List<Film> findFilmByTitre(final String titre);
	Film findFilmByTitreWithoutSpecialsCharacters(final String titre);
	List<Film> getAllRippedFilms();
	void removeFilm(Film film);
	Dvd buildDvd(final Integer annee,final Integer zone,final String edition,final Date ripDate,final DvdFormat dvdFormat, final String dateSortieDvd) throws ParseException;
	Boolean checkIfTmdbFilmExists(final Long tmdbId);
	Genre findGenre(final int id);
	Genre saveGenre(final Genre genre);
	List<Genre> findAllGenres();
	List<Film> findAllFilmsByFilmDisplayType(FilmDisplayTypeParam filmDisplayTypeParam);
	void cleanAllCaches();
	List<Personne> findAllRealisateurs(FilmDisplayTypeParam filmDisplayTypeParam);
	List<Personne> findAllActeurs(FilmDisplayTypeParam filmDisplayTypeParam);
	List<Personne> findAllActeursByFilmDisplayType(FilmDisplayTypeParam filmDisplayTypeParam);
	List<Personne> findAllRealisateursByFilmDisplayType(FilmDisplayTypeParam filmDisplayTypeParam);
	FilmListParam findFilmListParamByFilmDisplayType(final FilmDisplayTypeParam filmDisplayTypeParam);
	List<Film> search(String query,Integer offset,Integer limit,String sort);
	Page<Film> paginatedSarch(String query,Integer offset,Integer limit,String sort);
	List<Film> findFilmByOrigine(final FilmOrigine origine);
}
