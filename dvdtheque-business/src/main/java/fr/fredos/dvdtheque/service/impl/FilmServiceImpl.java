package fr.fredos.dvdtheque.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.dto.FilmDto;
@Service("filmService")
public class FilmServiceImpl implements IFilmService {
	protected Logger logger = LoggerFactory.getLogger(FilmServiceImpl.class);
	private static final String REALISATEUR_MESSAGE_WARNING = "Film should contains one producer";
	private static final String ACTEURS_MESSAGE_WARNING = "Film should contains actors";
	public static final String CACHE_FILM = "filmCache";
	
	@Autowired
	private FilmDao filmDao;
	@Autowired
	private IPersonneService personneService;
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
		return filmDao.findFilmByTitre(titre);
	}
	@Transactional(readOnly = true)
	public Film findFilmWithAllObjectGraph(Long id)  {
		return filmDao.findFilmWithAllObjectGraph(id);
	}
	@Transactional(readOnly = true)
	public Film findFilm(Long id) {
		return filmDao.findFilm(id);
	}
	@CacheEvict(value= CACHE_FILM, allEntries = true)
	@Transactional(readOnly = false)
	public void updateFilm(Film film){
		upperCaseTitre(film);
		if(film.isRipped()) {
			if(film.getDvd().getDateRip() == null) {
				film.getDvd().setDateRip(new Date());
			}
		}else {
			film.getDvd().setDateRip(null);
		}
		filmDao.updateFilm(film);
	}
	private void upperCaseTitre(Film film) {
		final String titre = StringUtils.upperCase(film.getTitre());
		film.setTitre(titre);
		final String titreO = StringUtils.upperCase(film.getTitreO());
		film.setTitreO(titreO);
	}
	@CacheEvict(value= CACHE_FILM, allEntries = true)
	@Transactional(readOnly = false)
	public Long saveNewFilm(Film film) {
		Assert.notEmpty(film.getRealisateurs(), REALISATEUR_MESSAGE_WARNING);
		upperCaseTitre(film);
		return filmDao.saveNewFilm(film);
	}
	@Transactional(readOnly = false)
	@Cacheable(value= "filmCache")
	public List<Film> findAllFilms() {
		return filmDao.findAllFilms();
	}
	@CacheEvict(value= CACHE_FILM, allEntries = true)
	@Transactional(readOnly = false)
	public void cleanAllFilms() {
		filmDao.cleanAllFilms();
		//personneService.cleanAllPersonnes();
	}
	@Transactional(readOnly = true)
	public List<Film> getAllRippedFilms(){
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
	@CacheEvict(value= CACHE_FILM, allEntries = true)
	@Transactional(readOnly = false)
	public void removeFilm(Film film) {
		film = filmDao.findFilm(film.getId());
		filmDao.removeFilm(film);
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
	public Dvd buildDvd(final Integer annee,final Integer zone,final String edition, Date ripDate) {
		Dvd dvd = new Dvd();
		if(annee != null) {
			dvd.setAnnee(annee);
		}
		if(zone != null) {
			dvd.setZone(zone);
		}else {
			dvd.setZone(1);
		}
		if(StringUtils.isEmpty(edition)) {
			dvd.setEdition("edition");
		}else {
			dvd.setEdition(edition);
		}
		dvd.setZone(1);
		if(ripDate!=null) {
			dvd.setDateRip(clearDate(ripDate));
		}
		return dvd;
	}
	
	/** TEST PURPOSE **/
	
	private Set<Personne> buildActeurs(final Personne act1,final Personne act2,final Personne act3){
		Set<Personne> acteurs = new HashSet<>();
		acteurs.add(act1);
		if(act2!=null) {
			acteurs.add(act2);
		}
		if(act3!=null) {
			acteurs.add(act3);
		}
		return acteurs;
	}
	private Set<Personne> buildRealisateurs(final Personne realisateur){
		Set<Personne> realisateurs = new HashSet<>();
		realisateurs.add(realisateur);
		return realisateurs;
	}
	private Film buildFilm(final String titre,
			final Integer annee,
			final Personne realisateur,
			final Personne act1,
			final Personne act2,
			final Personne act3,
			final Date ripDate) {
		Film film = new Film();
		film.setAnnee(annee);
		film.setRipped(true);
		film.setTitre(titre);
		film.setTitreO(titre);
		film.setDvd(buildDvd(annee,null,null, ripDate));
		film.setRealisateurs(buildRealisateurs(realisateur));
		film.setActeurs(buildActeurs(act1,act2,act3));
		film.setTmdbId(new Long(100));
		film.setOverview("Overview");
		return film;
	}
	//@Cacheable(value= "filmCache")
	@Override
	public Film createOrRetrieveFilm(final String titre,final Integer annee,
			final String realNom,
			final String act1Nom,
			final String act2Nom,
			final String act3Nom, 
			final Date ripDate) {
		Film film = findFilmByTitre(titre);
		if(film == null) {
			return createFilm(titre,annee, realNom, act1Nom, act2Nom, act3Nom, ripDate);
		}
		return film;
	}
	private Film createFilm(final String titre,
			final Integer annee,
			final String realNom,
			final String act1Nom,
			final String act2Nom,
			final String act3Nom,
			final Date ripDate) {
		Personne realisateur = null;
		Personne acteur1 = null;
		Personne acteur2 = null;
		Personne acteur3 = null;
		realisateur = personneService.createOrRetrievePersonne(realNom);
		acteur1 = personneService.createOrRetrievePersonne(act1Nom);
		acteur2 = personneService.createOrRetrievePersonne(act2Nom);
		acteur3 = personneService.createOrRetrievePersonne(act3Nom);
		Film film = buildFilm(titre,annee,realisateur,acteur1,acteur2,acteur3, ripDate);
		Long idFilm = saveNewFilm(film);
		film.setId(idFilm);
		return film;
	}
}
