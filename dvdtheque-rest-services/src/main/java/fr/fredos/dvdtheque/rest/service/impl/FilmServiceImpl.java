package fr.fredos.dvdtheque.rest.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.specifications.filter.PageRequestBuilder;
import fr.fredos.dvdtheque.rest.dao.domain.Dvd;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.repository.DvdDao;
import fr.fredos.dvdtheque.rest.dao.repository.FilmDao;
import fr.fredos.dvdtheque.rest.dao.repository.GenreDao;
import fr.fredos.dvdtheque.rest.dao.specifications.filter.SpecificationsBuilder;
import fr.fredos.dvdtheque.rest.exception.FilmNotFoundException;
import fr.fredos.dvdtheque.rest.service.IFilmService;
import fr.fredos.dvdtheque.rest.service.IPersonneService;
import fr.fredos.dvdtheque.rest.service.model.FilmDto;

@Service("filmService")
@CacheConfig(cacheNames = "films")
public class FilmServiceImpl implements IFilmService {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceImpl.class);
	private static final String REALISATEUR_MESSAGE_WARNING = "Film should contains one producer";

	public static final String CACHE_GENRE = "genreCache";
	IMap<Long, Genre> mapGenres;
	
	private final FilmDao filmDao;
	private final GenreDao genreDao;
	private final IPersonneService personneService;
	private final HazelcastInstance instance;
	
	@Autowired
	private SpecificationsBuilder<Film> builder;
	
	public FilmServiceImpl(FilmDao filmDao,DvdDao dvdDao,GenreDao genreDao,IPersonneService personneService,HazelcastInstance instance) {
		this.filmDao = filmDao;
		this.genreDao = genreDao;
		this.personneService = personneService;
		this.instance = instance;
		this.init();
	}
	
	public void init() {
		mapGenres = instance.getMap(CACHE_GENRE);
	}

	@Transactional(readOnly = true)
	public List<FilmDto> getAllFilmDtos() {
		List<Film> filmList = null;
		List<FilmDto> filmDtoList = new ArrayList<>();
		try {
			filmList = filmDao.findAll();
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
	public Film findFilmByTitreWithoutSpecialsCharacters(final String titre) {
		return filmDao.findFilmByTitreWithoutSpecialsCharacters(titre);
	}

	@Override
	@Transactional(readOnly = true)
	public Film findFilm(final Long id) {
		return filmDao.findById(id).orElseThrow(()->new FilmNotFoundException(String.format("film with id %s not found", id)));
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Film> findAllFilmByOrigine(final FilmOrigine origine) {
		var page = buildDefaultPageRequest(1, 10, "-dateSortie");
		return filmDao.findAll(builder.with("origine:eq:"+origine+":AND,").build(), page);
	}
	@Override
	@Transactional(readOnly = true)
	public Page<Film> findAllFilmByDvdFormat(final DvdFormat format) {
		var page = buildDefaultPageRequest(1, 10, "-dateSortie");
		return filmDao.findAll(builder.with("dvd.format:eq:"+format+":AND,").build(), page);
	}
	@Override
	@Transactional(readOnly = true)
	public Genre findGenre(int tmdbId) {
		return genreDao.findGenreByTmdbId(tmdbId);
	}

	@Override
	@Transactional(readOnly = false)
	public Film updateFilm(Film film) {
		upperCaseTitre(film);
		film.setDateMaj(LocalDateTime.now());
		var filmRetrieved = findFilm(film.getId());
		if(filmRetrieved.getOrigine() == FilmOrigine.DVD && film.getOrigine() != FilmOrigine.DVD) {
			filmRetrieved.setDvd(null);
		}
		if (film.getDvd() != null && film.getOrigine() == FilmOrigine.DVD) {
			filmRetrieved.setDvd(film.getDvd());
			if (!film.getDvd().isRipped()) {
				filmRetrieved.getDvd().setDateRip(null);
			}
		}
		if(film.getDvd() != null && filmRetrieved.getOrigine() == FilmOrigine.EN_SALLE) {
			filmRetrieved.setDvd(film.getDvd());
			filmRetrieved.getDvd().setDateRip(null);
		}
		filmRetrieved.setOrigine(film.getOrigine());
		filmRetrieved.setDateInsertion(film.getDateInsertion());
		filmRetrieved.setDateSortieDvd(film.getDateSortieDvd());
		filmRetrieved.setVu(film.isVu());
		if(!filmRetrieved.isVu()) {
			filmRetrieved.setDateVue(null);
		}else {
			filmRetrieved.setDateVue(film.getDateVue());
		}
		filmRetrieved.setAllocineFicheFilmId(film.getAllocineFicheFilmId());
		var mergedFilm = filmDao.save(filmRetrieved);
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
		Assert.notEmpty(film.getRealisateur(), REALISATEUR_MESSAGE_WARNING);
		upperCaseTitre(film);
		Film savedFilm = filmDao.save(film);
		return savedFilm.getId();
	}
	
	@Override
	@Transactional(readOnly = false)
	public Genre saveGenre(final Genre genre) {
		Genre persistedGenre = genreDao.save(genre);
		mapGenres.putIfAbsent(persistedGenre.getId(), persistedGenre);
		return persistedGenre;
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
		List<Genre> e = this.genreDao.findAll();
		logger.debug("genres size: " + e.size());
		if(CollectionUtils.isNotEmpty(e)) {
			e.parallelStream().forEach(it -> {
				mapGenres.putIfAbsent(it.getId(), it);
			});
		}
		return e;
	}

	@Override
	@Transactional(readOnly = false)
	public void cleanAllFilms() {
		filmDao.deleteAll();
		genreDao.deleteAll();
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
	public List<Film> search(String query,Integer offset,Integer limit,String sort){
		Integer limitToSet;
		Integer offsetToSet;
		String sortToSet;
		if(limit == null) {
			limitToSet = Integer.valueOf(50);
		}else {
			limitToSet = limit;
		}
		if(offset == null) {
			offsetToSet = Integer.valueOf(1);
		}else {
			offsetToSet = offset;
		}
		if(StringUtils.isEmpty(sort)) {
			sortToSet = "-dateInsertion";
		}else {
			sortToSet = sort;
		}
		return filmDao.findAll(builder.with(query).build(), 
				PageRequestBuilder.getPageRequest(limitToSet,offsetToSet, sortToSet)).getContent();
	}
	private PageRequest buildDefaultPageRequest(Integer offset,
			Integer limit,
			String sort) {
		Integer limitToSet;
		Integer offsetToSet;
		String sortToSet;
		if(limit == null) {
			limitToSet = Integer.valueOf(50);
		}else {
			limitToSet = limit;
		}
		if(offset == null) {
			offsetToSet = Integer.valueOf(1);
		}else {
			offsetToSet = offset;
		}
		if(StringUtils.isEmpty(sort)) {
			sortToSet = "-dateInsertion";
		}else {
			sortToSet = sort;
		}
		return PageRequestBuilder.getPageRequest(limitToSet,offsetToSet, sortToSet);
	}
	@Override
	@Transactional(readOnly = true)
	public Page<Film> paginatedSarch(String query,
			Integer offset,
			Integer limit,
			String sort){
		var page = buildDefaultPageRequest(offset, limit, sort);
		if(StringUtils.isEmpty(query)) {
			return filmDao.findAll(page);
		}
        return filmDao.findAll(builder.with(query).build(), page);
	}
	@Override
	public List<Film> findFilmByOrigine(final FilmOrigine origine){
		return filmDao.findFilmByOrigine(origine);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void removeFilm(Film film) {
		//film = mapFilms.get(film.getId());
		Optional<Film> filmOpt = filmDao.findById(film.getId());
		if(filmOpt.isPresent()) {
			filmDao.delete(filmOpt.get());
			//mapFilms.remove(filmOpt.get().getId());
		}
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
			final DvdFormat dvdFormat) throws ParseException {
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
		return dvd;
	}
	@Override
	public void cleanAllCaches() {
		mapGenres.clear();
	}
	@Override
	public Boolean checkIfTmdbFilmExists(final Long tmdbId) {
		return filmDao.checkIfTmdbFilmExists(tmdbId).equals(Integer.valueOf(1))?Boolean.TRUE:Boolean.FALSE;
	}
}
