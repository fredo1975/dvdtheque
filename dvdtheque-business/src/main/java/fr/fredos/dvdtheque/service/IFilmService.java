package fr.fredos.dvdtheque.service;

import java.util.List;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Film;

public interface IFilmService {
	public static final String CACHE_DIST_FILM = "dist-film";
	
	
	public final static String MAX_REALISATEUR_ID_SQL = "select max(p.id) from PERSONNE p inner join REALISATEUR r on r.ID_PERSONNE=p.ID";
	public final static String MAX_ACTEUR_ID_SQL = "select max(p.id) from PERSONNE p inner join ACTEUR a on a.ID_PERSONNE=p.ID";
	public final static String MAX_PERSONNE_ID_SQL = "select max(id) from PERSONNE";
	public final static String MAX_ID_FILM_SQL = "select max(id) from FILM";
	
	public Film findFilm(Integer id);
	public Film findFilmWithAllObjectGraph(Integer id);
	public List<Film> findAllFilms();
	public Set<Long> findAllTmdbFilms(Set<Long> tmdbIds);
	public void updateFilm(Film film);
	public Integer saveNewFilm(Film film);
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
			final String act3Nom);
}
