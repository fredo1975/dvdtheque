package fr.fredos.dvdtheque.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.enums.PersonneType;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.dao.model.repository.PersonneDao;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.dto.FilmDto;

@Service("filmService")
@CacheConfig(cacheNames = "films")
public class FilmServiceImpl implements IFilmService {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceImpl.class);
	private static final String REALISATEUR_MESSAGE_WARNING = "Film should contains one producer";
	// private static final String ACTEURS_MESSAGE_WARNING = "Film should contains
	// actors";
	public static final String CACHE_ACTEUR = "actCache";
	public static final String CACHE_REALISATEUR = "realCache";
	public static final String CACHE_ACTEUR_BY_ORIGINE = "actCacheByOrigine";
	public static final String CACHE_REALISATEUR_BY_ORIGINE = "realCacheByOrigine";
	public static final String CACHE_FILM = "films";
	public static final String CACHE_GENRE = "genreCache";
	IMap<Long, Film> mapFilms;
	IMap<Long, Genre> mapGenres;
	IMap<Long, Personne> mapRealisateurs;
	IMap<Long, Personne> mapActeurs;
	IMap<FilmOrigine, Map<Long,Set<Personne>>> mapActeursByOrigine;
	IMap<FilmOrigine, Map<Long,Set<Personne>>> mapRealisateursByOrigine;
	
	@Autowired
	private FilmDao filmDao;
	@Autowired
	private IPersonneService personneService;
	@Autowired
	private HazelcastInstance instance;
	@Autowired
	private PersonneDao personneDao;
	
	@PostConstruct
	public void init() {
		mapFilms = instance.getMap(CACHE_FILM);
		mapFilms.addIndex("id", true);
		mapFilms.addIndex("origine", false);
		mapFilms.addIndex("tmdbId", false);
		// logger.info("films cache: " + mapFilms.size());
		mapGenres = instance.getMap(CACHE_GENRE);
		mapGenres.addIndex("id", true);
		mapRealisateurs = instance.getMap(CACHE_REALISATEUR);
		mapRealisateurs.addIndex("id", true);
		mapRealisateurs.addIndex("nom", false);
		mapActeurs = instance.getMap(CACHE_ACTEUR);
		mapActeurs.addIndex("id", true);
		mapActeurs.addIndex("nom", false);
		mapActeursByOrigine = instance.getMap(CACHE_ACTEUR_BY_ORIGINE);
		//mapActeursByOrigine.addIndex("id", true);
		mapRealisateursByOrigine = instance.getMap(CACHE_REALISATEUR_BY_ORIGINE);
		//mapRealisateursByOrigine.addIndex("id", true);
		//findAllFilms();
	}

	@Transactional(readOnly = true)
	public List<FilmDto> getAllFilmDtos() {
		List<Film> filmList = null;
		List<FilmDto> filmDtoList = new ArrayList<>();
		try {
			filmList = filmDao.findAllFilms();
			if (!CollectionUtils.isEmpty(filmList)) {
				logger.debug("####################   filmList.size()=" + filmList.size());
				for (Film film : filmList) {
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

	@Transactional(readOnly = true, noRollbackFor = { org.springframework.dao.EmptyResultDataAccessException.class })
	public Film findFilmByTitre(String titre) {
		return filmDao.findFilmByTitre(titre);
	}

	@Transactional(readOnly = true)
	public Film findFilmWithAllObjectGraph(Long id) {
		return filmDao.findFilmWithAllObjectGraph(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Film findFilm(Long id) {
		Film film = mapFilms.get(id);
		if (film != null)
			return film;
		film = filmDao.findFilm(id);
		mapFilms.put(id, film);
		return film;
	}

	@Override
	@Transactional(readOnly = true)
	public Genre findGenre(int tmdbId) {
		return filmDao.findGenre(tmdbId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateFilm(Film film) {
		upperCaseTitre(film);
		if (film.getDvd() != null && !film.getDvd().isRipped()) {
			film.getDvd().setDateRip(null);
		}
		filmDao.updateFilm(film);
		mapFilms.put(film.getId(), film);
		
		handleMapPersonneByOrigine(PersonneType.ACTEUR, mapActeursByOrigine, film);
		handleMapPersonneByOrigine(PersonneType.REALISATEUR, mapRealisateursByOrigine, film);
		
	}

	private void upperCaseTitre(Film film) {
		final String titre = StringUtils.upperCase(film.getTitre());
		film.setTitre(titre);
		final String titreO = StringUtils.upperCase(film.getTitreO());
		film.setTitreO(titreO);
	}

	@Override
	@Transactional(readOnly = false)
	public Long saveNewFilm(Film film) {
		Assert.notEmpty(film.getRealisateurs(), REALISATEUR_MESSAGE_WARNING);
		upperCaseTitre(film);
		Long id = filmDao.saveNewFilm(film);
		mapFilms.put(id, film);
		
		handleMapPersonneByOrigine(PersonneType.ACTEUR,mapActeursByOrigine, film);
		
		handleMapPersonneByOrigine(PersonneType.REALISATEUR,mapRealisateursByOrigine, film);
		
		return id;
	}
	
	private void handleMapPersonneByOrigine(PersonneType personneType,IMap<FilmOrigine, Map<Long,Set<Personne>>> mapPersonnesByOrigine, final Film film) {
		Map<Long,Set<Personne>> personnesByFilm;
		if(mapPersonnesByOrigine.size()>0 && mapPersonnesByOrigine.containsKey(film.getOrigine())) {
			personnesByFilm = mapPersonnesByOrigine.get(film.getOrigine());
		}else {
			personnesByFilm = new HashMap<>();
		}
		personnesByFilm.put(film.getId(), PersonneType.ACTEUR.equals(personneType)?film.getActeurs():film.getRealisateurs());
		mapPersonnesByOrigine.put(film.getOrigine(), personnesByFilm);
	}

	@Override
	@Transactional(readOnly = false)
	public Genre saveGenre(Genre genre) {
		Genre persistedGenre = filmDao.saveGenre(genre);
		mapGenres.put(persistedGenre.getId(), persistedGenre);
		return persistedGenre;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Film> findAllFilms() {
		Collection<Film> films = mapFilms.values();
		logger.info("films cache size: " + films.size());
		if (films.size() > 0) {
			List<Film> l = films.stream().collect(Collectors.toList());
			Collections.sort(l, (f1,f2)->f1.getTitre().compareTo(f2.getTitre()));
			return l;
		}
		logger.info("no films find");
		List<Film> filmList = this.filmDao.findAllFilms();
		logger.info("filmList size: " + filmList.size());
		filmList.parallelStream().forEach(it -> {
			mapFilms.putIfAbsent(it.getId(), it);
		});
		return filmList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Genre> findAllGenres() {
		Collection<Genre> genres = mapGenres.values();
		logger.info("genres cache size: " + genres.size());
		if (genres.size() > 0) {
			List<Genre> l = genres.stream().collect(Collectors.toList());
			Collections.sort(l, (f1,f2)->f1.getName().compareTo(f2.getName()));
			return l;
		}
		logger.info("no genres find");
		List<Genre> e = this.filmDao.findAllGenres();
		logger.info("genres size: " + e.size());
		e.parallelStream().forEach(it -> {
			mapGenres.putIfAbsent(it.getId(), it);
		});
		return e;
	}

	@Override
	@Transactional(readOnly = false)
	public void cleanAllFilms() {
		filmDao.cleanAllFilms();
		mapFilms.clear();
		filmDao.cleanAllGenres();
		mapGenres.clear();
		mapActeursByOrigine.clear();
		mapRealisateursByOrigine.clear();
		personneService.cleanAllPersonnes();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Film> getAllRippedFilms() {
		return filmDao.getAllRippedFilms();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto) {
		List<Film> filmList = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		filmList.sort(Comparator.comparing(Film::getTitre));
		return filmList;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeFilm(Film film) {
		//film = mapFilms.get(film.getId());
		film = filmDao.findFilm(film.getId());
		filmDao.removeFilm(film);
		mapFilms.remove(film.getId());
	}

	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);
		byte[] b = new byte[2048];
		int length;
		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}
		is.close();
		os.close();
	}

	@Override
	@Transactional(readOnly = true)
	public Set<Long> findAllTmdbFilms(final Set<Long> tmdbIds) {
		return filmDao.findAllTmdbFilms(tmdbIds);
	}

	@Override
	public Date clearDate(Date dateToClear) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToClear);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		return cal.getTime();
	}

	@Override
	@Transactional(readOnly = true)
	public Dvd buildDvd(final Integer annee, final Integer zone, final String edition, Date ripDate,
			DvdFormat dvdFormat) {
		Dvd dvd = new Dvd();
		if (annee != null) {
			dvd.setAnnee(annee);
		}
		if (zone != null) {
			dvd.setZone(zone);
		} else {
			dvd.setZone(1);
		}
		if (StringUtils.isEmpty(edition)) {
			dvd.setEdition("edition");
		} else {
			dvd.setEdition(edition);
		}
		if (ripDate != null) {
			dvd.setDateRip(clearDate(ripDate));
		}
		if (dvdFormat != null) {
			dvd.setFormat(dvdFormat);
		}
		return dvd;
	}

	@Override
	public Boolean checkIfTmdbFilmExists(Long tmdbId) {
		return this.filmDao.checkIfTmdbFilmExists(tmdbId);
	}

	@Override
	@Transactional(readOnly = true)
	public Genre attachToSession(Genre genre) {
		return this.filmDao.attachToSession(genre);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Film> findAllFilmsByOrigine(FilmOrigine filmOrigine) {
		Predicate<Long, Film> predicate = Predicates.equal("origine", filmOrigine);
		logger.info("films cache find");
		Collection<Film> films = mapFilms.values(predicate);
		logger.info("films cache size: " + films.size());
		if (films.size() > 0) {
			return films.stream().collect(Collectors.toList());
		}
		logger.info("no films find");
		List<Film> e = this.filmDao.findAllFilmsByOrigine(filmOrigine);
		logger.info("films size: " + e.size());
		e.parallelStream().forEach(it -> {
			mapFilms.putIfAbsent(it.getId(), it);
		});
		return e;
	}

	@Override
	public void cleanAllCaches() {
		mapFilms.clear();
		mapGenres.clear();
		mapActeurs.clear();
		mapRealisateurs.clear();
		mapActeursByOrigine.clear();
		mapRealisateursByOrigine.clear();
	}
	
	@Override
	public List<Personne> findAllRealisateurs() {
		Collection<Personne> personnes = mapRealisateurs.values();
		logger.info("personnes cache size: " + personnes.size());
		if (personnes.size() > 0) {
			List<Personne> l = personnes.stream().collect(Collectors.toList());
			Collections.sort(l, (f1,f2)->f1.getNom().compareTo(f2.getNom()));
			return l;
		}
		logger.info("personnes find");
		List<Personne> personnesList = this.personneDao.findAllRealisateur();
		logger.info("personnesList size: " + personnesList.size());
		personnesList.parallelStream().forEach(it -> {
			mapRealisateurs.putIfAbsent(it.getId(), it);
		});
		return personnesList;
	}
	
	@Override
	public List<Personne> findAllRealisateursByOrigine(FilmOrigine filmOrigine) {
		Set<Personne> realisateursByOrigineToReturnSet = new ConcurrentSkipListSet<Personne>();
		if(mapRealisateursByOrigine.size()>0 && mapRealisateursByOrigine.containsKey(filmOrigine)) {
			Map<Long,Set<Personne>> realisateursByFilm = mapRealisateursByOrigine.get(filmOrigine);
			if (realisateursByFilm.size() > 0) {
				for(Set<Personne> set : realisateursByFilm.values()) {
					realisateursByOrigineToReturnSet.addAll(set);
				}
				List<Personne> realisateursByOrigineToReturn = new ArrayList<>(realisateursByOrigineToReturnSet);
				Collections.sort(realisateursByOrigineToReturn, (f1,f2)->f1.getNom().compareTo(f2.getNom()));
				return realisateursByOrigineToReturn;
			}
		}
		logger.info("no realisateurs by origine find");
		List<Film> l = findAllFilmsByOrigine(filmOrigine);
		logger.info("realisateursByOrigineToReturnSet size: " + realisateursByOrigineToReturnSet.size());
		Map<Long,Set<Personne>> map = new ConcurrentHashMap<>();
		l.parallelStream().forEach(film -> {
			map.put(film.getId(), film.getRealisateurs());
			realisateursByOrigineToReturnSet.addAll(film.getRealisateurs());
		});
		mapRealisateursByOrigine.put(filmOrigine, map);
		return new ArrayList<>(realisateursByOrigineToReturnSet);
	}
	
	@Override
	public List<Personne> findAllActeurs() {
		Collection<Personne> personnes = mapActeurs.values();
		logger.info("personnes cache size: " + personnes.size());
		if (personnes.size() > 0) {
			List<Personne> l = personnes.stream().collect(Collectors.toList());
			Collections.sort(l, (f1,f2)->f1.getNom().compareTo(f2.getNom()));
			return l;
		}
		logger.info("no personnes find");
		List<Personne> personnesList = this.personneDao.findAllActeur();
		logger.info("personnesList size: " + personnesList.size());
		personnesList.parallelStream().forEach(it -> {
			mapRealisateurs.putIfAbsent(it.getId(), it);
		});
		return personnesList;
	}
	@Override
	public List<Personne> findAllActeursByOrigine(FilmOrigine filmOrigine) {
		ConcurrentSkipListSet<Personne> acteursByOrigineToReturnSet = new ConcurrentSkipListSet<Personne>();
		if(mapActeursByOrigine.size()>0 && mapActeursByOrigine.containsKey(filmOrigine)) {
			Map<Long,Set<Personne>> acteursByFilm = mapActeursByOrigine.get(filmOrigine);
			logger.info("acteursByFilm cache size: " + acteursByFilm.size());
			if (acteursByFilm.size() > 0) {
				for(Set<Personne> set : acteursByFilm.values()) {
					acteursByOrigineToReturnSet.addAll(set);
				}
				return new ArrayList<>(acteursByOrigineToReturnSet);
			}
		}
		logger.info("no acteurs by origine find");
		List<Film> films = findAllFilmsByOrigine(filmOrigine);
		logger.info("acteursByOrigineToReturnSet size: " + acteursByOrigineToReturnSet.size());
		Map<Long,Set<Personne>> map = new ConcurrentHashMap<>();
		films.parallelStream().forEach(film -> {
			map.put(film.getId(), film.getActeurs());
			acteursByOrigineToReturnSet.addAll(film.getActeurs());
		});
		mapActeursByOrigine.put(filmOrigine, map);
		return new ArrayList<>(acteursByOrigineToReturnSet);
		
	}
}
