package fr.fredos.dvdtheque.service.impl;

import static java.lang.String.format;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmDisplayType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.enums.PersonneType;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.dao.model.object.CritiquesPresse;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.model.FilmDto;
import fr.fredos.dvdtheque.service.model.FilmListParam;
import fr.fredos.dvdtheque.service.model.FilmListParamBuilder;

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
	IMap<FilmOrigine, Map<Film,Set<Personne>>> mapActeursByOrigine;
	IMap<FilmOrigine, Map<Film,Set<Personne>>> mapRealisateursByOrigine;
	
	@Autowired
	private FilmDao filmDao;
	@Autowired
	private IPersonneService personneService;
	@Autowired
	private HazelcastInstance instance;
	
	@PostConstruct
	public void init() {
		mapFilms = instance.getMap(CACHE_FILM);
		mapFilms.addIndex("id", false);
		mapFilms.addIndex("origine", false);
		mapFilms.addIndex("tmdbId", false);
		// logger.info("films cache: " + mapFilms.size());
		mapGenres = instance.getMap(CACHE_GENRE);
		mapGenres.addIndex("id", true);
		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,40,FilmOrigine.TOUS);
		findAllFilms(filmDisplayTypeParam);
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
	public Film findFilmByTitre(final String titre) {
		return filmDao.findFilmByTitre(titre);
	}
	
	@Transactional(readOnly = true, noRollbackFor = { org.springframework.dao.EmptyResultDataAccessException.class })
	public Film findFilmByTitreWithoutSpecialsCharacters(final String titre) {
		return filmDao.findFilmByTitreWithoutSpecialsCharacters(titre);
	}

	@Transactional(readOnly = true)
	public Film findFilmWithAllObjectGraph(final Long id) {
		return filmDao.findFilmWithAllObjectGraph(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Film findFilm(final Long id) {
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
	public Film updateFilm(Film film) {
		upperCaseTitre(film);
		if (film.getDvd() != null && !film.getDvd().isRipped()) {
			film.getDvd().setDateRip(null);
		}
		Film mergedFilm = filmDao.updateFilm(film);
		Set<CritiquesPresse> newSortedCritiquesPresseSet = new TreeSet<>(mergedFilm.getCritiquesPresse());
		mergedFilm.getCritiquesPresse().clear();
		mergedFilm.getCritiquesPresse().addAll(newSortedCritiquesPresseSet);
		mapFilms.put(film.getId(), mergedFilm);
		return mergedFilm;
	}

	private void upperCaseTitre(final Film film) {
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
		return id;
	}
	
	@Override
	@Transactional(readOnly = false)
	public Genre saveGenre(final Genre genre) {
		Genre persistedGenre = filmDao.saveGenre(genre);
		mapGenres.put(persistedGenre.getId(), persistedGenre);
		return persistedGenre;
	}
	
	@Override
	@Transactional(readOnly = false)
	public CritiquesPresse saveCritiquesPresse(final CritiquesPresse critiquesPresse) {
		CritiquesPresse persistedCritiquesPresse = filmDao.saveCritiquesPresse(critiquesPresse);
		return persistedCritiquesPresse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Film> findAllFilms(FilmDisplayTypeParam filmDisplayTypeParam) {
		Collection<Film> films = mapFilms.values();
		logger.debug("findAllFilms films cache size: " + films.size());
		if (films.size() > 0) {
			return sortListAccordingToFilmDisplayType(films,filmDisplayTypeParam);
		}
		logger.debug("no films find");
		List<Film> filmList = this.filmDao.findAllFilms();
		logger.debug("filmList size: " + filmList.size());
		filmList.parallelStream().forEach(it -> {
			mapFilms.putIfAbsent(it.getId(), it);
		});
		if(filmDisplayTypeParam == null || FilmDisplayType.TOUS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			return filmList;
		}
		return sortListAccordingToFilmDisplayType(filmList,filmDisplayTypeParam);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Genre> findAllGenres() {
		Collection<Genre> genres = mapGenres.values();
		logger.debug("genres cache size: " + genres.size());
		if (genres.size() > 0) {
			List<Genre> l = genres.stream().collect(Collectors.toList());
			Collections.sort(l, (f1,f2)->f1.getName().compareTo(f2.getName()));
			return l;
		}
		logger.debug("no genres find");
		List<Genre> e = this.filmDao.findAllGenres();
		logger.debug("genres size: " + e.size());
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
		filmDao.cleanAllCritiquesPresse();
		mapGenres.clear();
		personneService.cleanAllPersonnes();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Film> getAllRippedFilms() {
		return filmDao.getAllRippedFilms();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Film> findAllFilmsByCriteria(final FilmFilterCriteriaDto filmFilterCriteriaDto) {
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

	public static void saveImage(final String imageUrl, final String destinationFile) throws IOException {
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
	public void saveImageToFilmPosterAsByteArray(final String imageUrl, final Film film) throws IOException {
		try {
			URL url = new URL(imageUrl);
			InputStream is = url.openStream();
			byte[] bytesArr = IOUtils.toByteArray(is);
			film.setPoster(bytesArr);
			is.close();
		}catch(FileNotFoundException e) {
			logger.error(format("file '%s' doesn't exists", imageUrl));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Set<Long> findAllTmdbFilms(final Set<Long> tmdbIds) {
		return filmDao.findAllTmdbFilms(tmdbIds);
	}

	@Override
	public Date clearDate(final Date dateToClear) {
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
	public Dvd buildDvd(final Integer annee, final Integer zone, final String edition, final Date ripDate,
			final DvdFormat dvdFormat, final String dateSortieDvd) throws ParseException {
		Dvd dvd = new Dvd();
		if (annee != null) {
			dvd.setAnnee(annee);
		}
		if (zone != null) {
			dvd.setZone(zone);
		} else {
			dvd.setZone(21);
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
		if(StringUtils.isNotEmpty(dateSortieDvd)) {
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			dvd.setDateSortie(sdf.parse(dateSortieDvd));
		}
		return dvd;
	}

	@Override
	public Boolean checkIfTmdbFilmExists(final Long tmdbId) {
		return this.filmDao.checkIfTmdbFilmExists(tmdbId);
	}

	@Override
	@Transactional(readOnly = true)
	public Genre attachToSession(final Genre genre) {
		return this.filmDao.attachToSession(genre);
	}

	/**
	 * 
	 * @param films
	 * @return
	 */
	private List<Film> sortListAccordingToFilmDisplayType(Collection<Film> films, final FilmDisplayTypeParam filmDisplayTypeParam) {
		if(filmDisplayTypeParam==null || FilmDisplayType.TOUS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			List<Film> list = films.stream().collect(Collectors.toList());
			Collections.sort(list);
			return list;
		}else if(FilmDisplayType.DERNIERS_AJOUTS_NON_VUS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			return films.stream().sorted(Comparator.comparing(Film::getDateInsertion).reversed()).filter(f->!f.isVu()).limit(filmDisplayTypeParam.getLimitFilmSize()).collect(Collectors.toList());
		}else {
			return films.stream().sorted(Comparator.comparing(Film::getDateInsertion).reversed()).limit(filmDisplayTypeParam.getLimitFilmSize()).collect(Collectors.toList());
		}
		
	}
	@Transactional(readOnly = true)
	@Override
	public List<Film> findAllFilmsByFilmDisplayType(final FilmDisplayTypeParam filmDisplayTypeParam) {
		StopWatch watch = new StopWatch();
		watch.start();
		if(filmDisplayTypeParam != null
				&& FilmOrigine.TOUS.equals(filmDisplayTypeParam.getFilmOrigine()) 
				&& FilmDisplayType.TOUS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			return findAllFilms(filmDisplayTypeParam);
		}else {
			Predicate<Long, Film> predicate = Predicates.equal("origine", filmDisplayTypeParam.getFilmOrigine());
			//logger.info("findAllFilmsByFilmDisplayType films cache find ");
			Collection<Film> films = mapFilms.values(predicate);
			logger.debug("findAllFilmsByFilmDisplayType films cache size: " + films.size());
			if (films.size() > 0) {
				watch.stop();
				logger.info("findAllFilmsByFilmDisplayType="+watch.getTotalTimeSeconds());
				return sortListAccordingToFilmDisplayType(films,filmDisplayTypeParam);
			}
			logger.debug("no films find");
			List<Film> filmsRes = this.filmDao.findAllFilmsByOrigine(filmDisplayTypeParam.getFilmOrigine());
			logger.debug("findAllFilmsByFilmDisplayType films size: " + filmsRes.size());
			filmsRes.parallelStream().forEach(it -> {
				mapFilms.putIfAbsent(it.getId(), it);
			});
			watch.stop();
			logger.info("findAllFilmsByFilmDisplayType="+watch.getTotalTimeMillis());
			return sortListAccordingToFilmDisplayType(filmsRes,filmDisplayTypeParam);
		}
	}
	
	@Override
	public void cleanAllCaches() {
		mapFilms.clear();
		mapGenres.clear();
	}
	
	@Override
	public List<Personne> findAllRealisateurs(FilmDisplayTypeParam filmDisplayTypeParam) {
		Set<Personne> realisateurs = new ConcurrentSkipListSet<Personne>();
		Collection<Film> films = mapFilms.values();
		logger.debug("findAllRealisateurs films cache size: " + films.size());
		if (films.size() > 0) {
			if(filmDisplayTypeParam!=null && FilmDisplayType.DERNIERS_AJOUTS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
				List<Film> sortedFilms = sortListAccordingToFilmDisplayType(films, filmDisplayTypeParam);
				return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.REALISATEUR,sortedFilms, realisateurs);
			}
			return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.REALISATEUR,films, realisateurs);
		}
		logger.debug("findAllRealisateurs no films find");
		List<Film> filmList = this.filmDao.findAllFilms();
		logger.debug("findAllRealisateurs filmList size: " + filmList.size());
		if(FilmDisplayType.DERNIERS_AJOUTS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			List<Film> sortedFilms = sortListAccordingToFilmDisplayType(films, filmDisplayTypeParam);
			return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.REALISATEUR,sortedFilms, realisateurs);
		}
		return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.REALISATEUR,filmList, realisateurs);
	}
	
	@Override
	public List<Personne> findAllRealisateursByFilmDisplayType(FilmDisplayTypeParam filmDisplayTypeParam) {
		StopWatch watch = new StopWatch();
		watch.start();
		Set<Personne> realisateursByOrigineToReturnSet = new TreeSet<Personne>();
		List<Film> films = findAllFilmsByFilmDisplayType(filmDisplayTypeParam);
		if(filmDisplayTypeParam==null || FilmDisplayType.TOUS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			realisateursByOrigineToReturnSet = findAllFilmsByFilmDisplayType(filmDisplayTypeParam).stream().map(Film::getRealisateurs).flatMap(x->x.stream()).collect(Collectors.toSet());
		}else {
			List<Film> sortedFilms = sortListAccordingToFilmDisplayType(films, filmDisplayTypeParam);
			for(Film film : sortedFilms) {
				realisateursByOrigineToReturnSet.addAll(film.getRealisateurs());
			}
		}
		watch.stop();
		logger.info("findAllRealisateursByFilmDisplayType="+watch.getTotalTimeMillis());
		return new ArrayList<>(realisateursByOrigineToReturnSet);
	}
	
	@Override
	public List<Personne> findAllActeurs(FilmDisplayTypeParam filmDisplayTypeParam) {
		Set<Personne> acteurs = new ConcurrentSkipListSet<Personne>();
		Collection<Film> films = mapFilms.values();
		logger.debug("findAllActeurs films cache size: " + films.size());
		if (films.size() > 0) {
			if(filmDisplayTypeParam!=null && FilmDisplayType.DERNIERS_AJOUTS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
				List<Film> sortedFilms = sortListAccordingToFilmDisplayType(films, filmDisplayTypeParam);
				return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.ACTEUR,sortedFilms, acteurs);
			}
			return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.ACTEUR,films, acteurs);
		}
		logger.debug("findAllActeurs no films find");
		List<Film> filmList = this.filmDao.findAllFilms();
		logger.debug("findAllActeurs filmList size: " + filmList.size());
		if(FilmDisplayType.DERNIERS_AJOUTS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			List<Film> sortedFilms = sortListAccordingToFilmDisplayType(films, filmDisplayTypeParam);
			return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.ACTEUR,sortedFilms, acteurs);
		}
		return iterateThroughFilmsToGetPersonnesListSorted(PersonneType.ACTEUR,filmList, acteurs);
	}
	
	private List<Personne> iterateThroughFilmsToGetPersonnesListSorted(final PersonneType personneType,final Collection<Film> films,final Set<Personne> personnes){
		films.parallelStream().forEach(it -> {
			personnes.addAll(PersonneType.ACTEUR.equals(personneType)?it.getActeurs():it.getRealisateurs());
		});
		return personnes.stream().collect(Collectors.toList());
	}
	
	@Override
	public List<Personne> findAllActeursByFilmDisplayType(final FilmDisplayTypeParam filmDisplayTypeParam) {
		StopWatch watch = new StopWatch();
		watch.start();
		Set<Personne> acteursByOrigineToReturnSet = new TreeSet<Personne>();
		List<Film> films = findAllFilmsByFilmDisplayType(filmDisplayTypeParam);
		if(filmDisplayTypeParam==null || FilmDisplayType.TOUS.equals(filmDisplayTypeParam.getFilmDisplayType())) {
			acteursByOrigineToReturnSet = findAllFilmsByFilmDisplayType(filmDisplayTypeParam).stream().map(Film::getActeurs).flatMap(x->x.stream()).collect(Collectors.toSet());
		}else {
			List<Film> sortedFilms = sortListAccordingToFilmDisplayType(films, filmDisplayTypeParam);
			for(Film film : sortedFilms) {
				acteursByOrigineToReturnSet.addAll(film.getActeurs());
			}
		}
		watch.stop();
		logger.info("findAllActeursByFilmDisplayType="+watch.getTotalTimeMillis());
		return new ArrayList<>(acteursByOrigineToReturnSet);
	}
	@Override
	public FilmListParam findFilmListParamByFilmDisplayType(final FilmDisplayTypeParam filmDisplayTypeParam) {
		StopWatch watch = new StopWatch();
		watch.start();
		List<Film> films = this.findAllFilmsByFilmDisplayType(filmDisplayTypeParam);
		Set<Personne> acteurs = films.stream().map(Film::getActeurs).flatMap(x->x.stream()).sorted().collect(Collectors.toSet());
		List<Personne> acteursList = new ArrayList<Personne>(acteurs);
		Collections.sort(acteursList);
		Set<Personne> realisateurs = films.stream().map(Film::getRealisateurs).flatMap(x->x.stream()).sorted().collect(Collectors.toSet());
		List<Personne> realisateursList = new ArrayList<Personne>(realisateurs);
		Collections.sort(realisateursList);
		int realisateursLength = realisateurs.size();
		int acteursLength = acteurs.size();
		FilmListParam filmListParam = new FilmListParamBuilder.Builder()
				.setFilms(films)
				.setActeurs(acteursList)
				.setRealisateurs(realisateursList)
				.setActeursLength(acteursLength)
				.setRealisateursLength(realisateursLength)
				.setGenres(this.findAllGenres())
				.build();
		watch.stop();
		logger.info("findFilmListParamByFilmDisplayType="+watch.getTotalTimeMillis());
		return filmListParam;
	}
}
