package fr.fredos.dvdtheque.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.dao.model.object.CritiquesPresse;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.model.FilmListParam;

public interface IFilmService {
	public static final String CACHE_DIST_FILM = "dist-film";
	Date clearDate(final Date dateToClear);
	Film findFilm(Long id);
	Film findFilmWithAllObjectGraph(final Long id);
	List<Film> findAllFilms(FilmDisplayTypeParam filmDisplayTypeParam);
	Set<Long> findAllTmdbFilms(final Set<Long> tmdbIds);
	Film updateFilm(Film film);
	Long saveNewFilm(Film film);
	//public List<FilmDto> getAllFilmDtos();
	void cleanAllFilms();
	Film findFilmByTitre(final String titre);
	Film findFilmByTitreWithoutSpecialsCharacters(final String titre);
	List<Film> getAllRippedFilms();
	List<Film> findAllFilmsByCriteria(final FilmFilterCriteriaDto filmFilterCriteriaDto);
	void removeFilm(Film film);
	Dvd buildDvd(final Integer annee,final Integer zone,final String edition,final Date ripDate,final DvdFormat dvdFormat, final String dateSortieDvd) throws ParseException;
	Boolean checkIfTmdbFilmExists(final Long tmdbId);
	Genre findGenre(final int id);
	Genre attachToSession(final Genre genre);
	Genre saveGenre(final Genre genre);
	CritiquesPresse saveCritiquesPresse(final CritiquesPresse critiquesPresse);
	List<Genre> findAllGenres();
	List<Film> findAllFilmsByFilmDisplayType(FilmDisplayTypeParam filmDisplayTypeParam);
	void cleanAllCaches();
	List<Personne> findAllRealisateurs(FilmDisplayTypeParam filmDisplayTypeParam);
	List<Personne> findAllActeurs(FilmDisplayTypeParam filmDisplayTypeParam);
	List<Personne> findAllActeursByFilmDisplayType(FilmDisplayTypeParam filmDisplayTypeParam);
	List<Personne> findAllRealisateursByFilmDisplayType(FilmDisplayTypeParam filmDisplayTypeParam);
	FilmListParam findFilmListParamByFilmDisplayType(final FilmDisplayTypeParam filmDisplayTypeParam);
}
