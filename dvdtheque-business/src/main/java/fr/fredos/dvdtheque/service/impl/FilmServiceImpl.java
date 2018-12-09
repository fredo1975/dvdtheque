package fr.fredos.dvdtheque.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.dto.NewActeurDto;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;
import fr.fredos.dvdtheque.service.dto.FilmDto;
@Service("filmService")
public class FilmServiceImpl implements FilmService {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceImpl.class);
	public static final String CACHE_DIST_FILM = "dist-film";
	private static final String REALISATEUR_MESSAGE_WARNING = "Film should contains one producer";
	private static final String ACTEURS_MESSAGE_WARNING = "Film should contains actors";
	@Autowired
	private FilmDao filmDao;
	@Autowired
	private PersonneService personneService;
	@Cacheable(value= "filmDtoCache")
	@Transactional(readOnly = true)
	public List<FilmDto> getAllFilmDtos() {
		List<Film> filmList = null;
		List<FilmDto> filmDtoList = new ArrayList<>();
		try {
			filmList = filmDao.findAllFilms();
			if(!CollectionUtils.isEmpty(filmList)){
				logger.debug("####################   filmList.size()="+filmList.size());
				for(Film film : filmList) {
					FilmDto filmDto = FilmDto.toDto(film);
					filmDtoList.add(filmDto);
				}
			}
		} catch (Exception e) {
			logger.error(e.getCause().getMessage());
		}
		filmDtoList.sort(Comparator.comparing(FilmDto::getTitre));
		return filmDtoList;
	}
	@Transactional(readOnly = true,noRollbackFor = { org.springframework.dao.EmptyResultDataAccessException.class })
	public Film findFilmByTitre(String titre){
		Film film = filmDao.findFilmByTitre(titre);
		//setRealisateur(film);
		return film;
	}
	@Transactional(readOnly = true)
	public Film findFilmWithAllObjectGraph(Integer id)  {
		Film film = filmDao.findFilmWithAllObjectGraph(id);
		setRealisateur(film);
		return film;
	}
	@Transactional(readOnly = true)
	public Film findFilm(Integer id) {
		Film f = filmDao.findFilm(id);
		setRealisateur(f);
		return f;
	}
	@CacheEvict(value= "filmCache")
	@Transactional(readOnly = false)
	public void updateFilm(Film film){
		Assert.notEmpty(film.getRealisateurs(), REALISATEUR_MESSAGE_WARNING);
		film.getRealisateurs().clear();
		film.getRealisateurs().add(film.getRealisateur());
		handleActeurs(film);
		upperCaseTitre(film);
		filmDao.updateFilm(film);
	}
	/**
	 * attach to session existing acteurs and persist new acteurs
	 * @param film
	 */
	private void handleActeurs(Film film) {
		if(CollectionUtils.isEmpty(film.getActeurs()) && CollectionUtils.isEmpty(film.getNewActeurDtoSet())) {
			Assert.notEmpty(film.getActeurs(), ACTEURS_MESSAGE_WARNING);
		}
		if(!CollectionUtils.isEmpty(film.getActeurs())) {
			Set<Personne> acteurs = new HashSet<>();
			for(Personne acteur : film.getActeurs()) {
				acteurs.add(personneService.getPersonne(acteur.getId()));
			}
			film.getActeurs().clear();
			film.setActeurs(acteurs);
		}
		handleNewActeurDtoSet(film);
	}
	private void upperCaseTitre(Film film) {
		final String titre = StringUtils.upperCase(film.getTitre());
		film.setTitre(titre);
		final String titreO = StringUtils.upperCase(film.getTitreO());
		film.setTitreO(titreO);
	}
	@Transactional(readOnly = false)
	public Integer saveNewFilm(Film film) {
		Assert.notEmpty(film.getRealisateurs(), REALISATEUR_MESSAGE_WARNING);
		handleRealisateur(film);
		handleActeurs(film);
		upperCaseTitre(film);
		return filmDao.saveNewFilm(film);
	}
	private void handleRealisateur(Film film) {
		Set<Personne> realisateurs = new HashSet<>();
		realisateurs.add(film.getRealisateurs().iterator().next());
		film.getRealisateurs().clear();
		film.getRealisateurs().add(personneService.getPersonne(realisateurs.iterator().next().getId()));
	}
	/**
	 * 
	 * @param film
	 */
	private void setRealisateur(Film film) {
		if(film.getRealisateurs()!=null) {
			film.setRealisateur(film.getRealisateurs().iterator().next());
		}
	}
	@Transactional(readOnly = false)
	public List<Film> findAllFilms() {
		List<Film> l = filmDao.findAllFilms();
		for(Film f : l) {
			setRealisateur(f);
		}
		return filmDao.findAllFilms();
	}
	@Transactional(readOnly = false)
	public void cleanAllFilms() {
		filmDao.cleanAllFilms();
	}
	@Transactional(readOnly = true)
	public List<Film> getAllRippedFilms(){
		List<Film> filmList = filmDao.getAllRippedFilms();
		for(Film f : filmList) {
			setRealisateur(f);
		}
		return filmList;
	}
	@Override
	public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto) {
		List<Film> filmList = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		for(Film f : filmList) {
			setRealisateur(f);
		}
		filmList.sort(Comparator.comparing(Film::getPrintRealisateur).thenComparing(Film::getTitre));
		return filmList;
	}
	@Override
	@Transactional(readOnly = false)
	public void removeFilm(Film film) {
		film = filmDao.findFilm(film.getId());
		filmDao.removeFilm(film);
	}
	/**
	 * film could contains new acteurs not persisted yet
	 * @param film
	 */
	private void handleNewActeurDtoSet(Film film) {
		if(!CollectionUtils.isEmpty(film.getNewActeurDtoSet())) {
			for(NewActeurDto newActeurDto : film.getNewActeurDtoSet()) {
				final String nom = StringUtils.upperCase(newActeurDto.getNom());
				final String prenom = StringUtils.upperCase(newActeurDto.getPrenom());
				Personne p = personneService.findPersonneByFullName(nom, prenom);
				if(p==null) {
					p = Personne.buildPersonneFromNewActeurDto(newActeurDto);
					personneService.savePersonne(p);
					film.getActeurs().add(p);
				}
			}
		}
	}
}
