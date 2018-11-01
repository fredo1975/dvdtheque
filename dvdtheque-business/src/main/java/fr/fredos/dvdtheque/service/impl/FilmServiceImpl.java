package fr.fredos.dvdtheque.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.dto.FilmDto;
@Service("filmService")
public class FilmServiceImpl implements FilmService {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceImpl.class);
	public static final String CACHE_DIST_FILM = "dist-film";
	@Autowired
	private FilmDao filmDao;

	@Cacheable(value= "filmCache")
	@Transactional(readOnly = true)
	public List<Film> getAllFilms() {
		List<Film> filmList = null;
		try {
			filmList = filmDao.findAllFilms();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return filmList;
	}
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
	public FilmDto findFilmByTitre(String titre){
		Film film = null;
		FilmDto filmDto = null;
		try {
			film = filmDao.findFilmByTitre(titre);
			if (null != film) {
				filmDto = FilmDto.toDto(film);
			}

		} catch (Exception e) {
			logger.error("",e);
			throw e;
		}
		if (null != filmDto) {
			logger.debug("end film=" + filmDto.toString());
		}
		
		return filmDto;
	}
	@Transactional(readOnly = true)
	public FilmDto findFilmWithAllObjectGraph(Integer id)  {
		Film film = null;
		FilmDto filmDto = null;
		film = filmDao.findFilmWithAllObjectGraph(id);
		if (null != film) {
			filmDto = FilmDto.toDto(film);
		}
		if (null != filmDto) {
			logger.debug("film=" + filmDto.toString());
		}
		
		return filmDto;
	}

	@Transactional(readOnly = true)
	public FilmDto findFilm(Integer id) {
		Film film = null;
		FilmDto filmDto = null;
		film = filmDao.findFilm(id);
		if (null != film) {
			filmDto = FilmDto.toDto(film);
		}
		if (null != filmDto) {
			logger.debug("end film=" + filmDto.toString());
		}
		return filmDto;
	}
	@CacheEvict(value= "filmCache")
	@Transactional(readOnly = false)
	public Film updateFilm(Film film){
		return filmDao.updateFilm(film);
	}
	@Transactional(readOnly = false)
	public FilmDto saveNewFilm(FilmDto filmDto) {
		String methodName = "saveNewFilm: ";
		//logger.debug(methodName + "start filmDto=" + filmDto.toString());
		Film filmRes = filmDto.fromDto();
		filmDao.saveNewFilm(filmRes);
		//logger.debug(methodName + "end");
		return FilmDto.toDto(filmRes);
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,isolation=Isolation.DEFAULT)
	public void saveNewFilm(Film film){
		filmDao.saveNewFilm(film);
	}
	/*
	@Transactional(readOnly = false)
	public DvdDto saveDvd(DvdDto dvdDto) throws Exception {
		DvdDto dvdDtoRes = null;
		try {
			Dvd dvd = buildDvd();
			filmDao.saveDvd(DvdDto.fromDto(dvdDto));
			dvdDtoRes = DvdDto.toDto(dvd);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception(e);
		}
		return dvdDtoRes;
	}

	private Dvd buildDvd() {
		Dvd dvd = new Dvd();
		return dvd;
	}*/

	/*
	private Film buildFilm(Integer id, FilmDto filmDto) {
		Film film = new Film();
		BeanUtils.copyProperties(filmDto, film);
		film.setId(filmDto.getId());
		film.setAnnee(filmDto.getAnnee());

		Set<Realisateur> realisateur = new HashSet<Realisateur>();
		realisateur.add(RealisateurDto.fromDto(filmDto.getPersonnesFilm().getRealisateur(), film));
		//film.setRealisateurs(realisateur);
		Set<Acteur> acteurs = new HashSet<Acteur>();
		for (ActeurDto acteurDto : filmDto.getPersonnesFilm().getActeurs()) {
			acteurs.add(ActeurDto.fromDto(acteurDto, film));
		}
		//film.setActeurs(acteurs);
		return film;
	}*/
	
	@Transactional(readOnly = false)
	public List<Film> findAllFilms() {
		return filmDao.findAllFilms();
	}
	@Transactional(readOnly = false)
	public void cleanAllFilms() {
		filmDao.cleanAllFilms();
	}
	@Transactional(readOnly = true)
	public List<Film> getAllRippedFilms(){
		return filmDao.getAllRippedFilms();
	}
	@Override
	public List<FilmDto> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto) {
		List<FilmDto> filmDtoList = new ArrayList<>();
		List<Film> filmList = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		logger.debug("####################   filmList.size()="+filmList.size());
		if(!CollectionUtils.isEmpty(filmList)){
			for(Film film : filmList) {
				FilmDto filmDto = FilmDto.toDto(film);
				filmDtoList.add(filmDto);
			}
		}
		filmDtoList.sort(Comparator.comparing(FilmDto::getPrintRealisateur).thenComparing(FilmDto::getTitre));
		return filmDtoList;
	}
	@Override
	@Transactional(readOnly = false)
	public void removeFilm(FilmDto filmDto) {
		Film film = filmDao.findFilm(filmDto.fromDto().getId());
		filmDao.removeFilm(film);
	}
}
