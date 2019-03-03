package fr.fredos.dvdtheque.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
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
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
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
	public Film findFilmWithAllObjectGraph(Integer id)  {
		return filmDao.findFilmWithAllObjectGraph(id);
	}
	@Transactional(readOnly = true)
	public Film findFilm(Integer id) {
		return filmDao.findFilm(id);
	}
	@CacheEvict(value= "filmCache", allEntries = true)
	@Transactional(readOnly = false)
	public void updateFilm(Film film){
		Assert.notEmpty(film.getRealisateurs(), REALISATEUR_MESSAGE_WARNING);
		handleActeurs(film);
		handleRealisateurs(film);
		upperCaseTitre(film);
		filmDao.updateFilm(film);
	}
	private void handleRealisateurs(Film film) {
		if(CollectionUtils.isEmpty(film.getRealisateurs())) {
			Assert.notEmpty(film.getRealisateurs(), REALISATEUR_MESSAGE_WARNING);
		}
		if(!CollectionUtils.isEmpty(film.getRealisateurs())) {
			Set<Personne> realisateurs = new HashSet<>();
			for(Personne realisateur : film.getRealisateurs()) {
				realisateurs.add(personneService.createOrRetrievePersonne(realisateur.getNom()));
			}
			film.getRealisateurs().clear();
			film.setRealisateurs(realisateurs);
		}
	}
	
	/**
	 * attach to session existing acteurs and persist new acteurs
	 * @param film
	 */
	private void handleActeurs(Film film) {
		if(CollectionUtils.isEmpty(film.getActeurs())) {
			Assert.notEmpty(film.getActeurs(), ACTEURS_MESSAGE_WARNING);
		}
		if(!CollectionUtils.isEmpty(film.getActeurs())) {
			Set<Personne> acteurs = new HashSet<>();
			for(Personne acteur : film.getActeurs()) {
				acteurs.add(personneService.createOrRetrievePersonne(acteur.getNom()));
			}
			film.getActeurs().clear();
			film.setActeurs(acteurs);
		}
		//handleNewActeurDtoSet(film);
	}
	
	private void upperCaseTitre(Film film) {
		final String titre = StringUtils.upperCase(film.getTitre());
		film.setTitre(titre);
		final String titreO = StringUtils.upperCase(film.getTitreO());
		film.setTitreO(titreO);
	}
	@CacheEvict(value= "filmCache", allEntries = true)
	@Transactional(readOnly = false)
	public Integer saveNewFilm(Film film) {
		Assert.notEmpty(film.getRealisateurs(), REALISATEUR_MESSAGE_WARNING);
		handleActeurs(film);
		handleRealisateurs(film);
		upperCaseTitre(film);
		return filmDao.saveNewFilm(film);
	}
	private void handleRealisateur(Film film) {
		Set<Personne> realisateurs = new HashSet<>();
		realisateurs.add(film.getRealisateurs().iterator().next());
		film.getRealisateurs().clear();
		film.getRealisateurs().add(personneService.getPersonne(realisateurs.iterator().next().getId()));
	}
	
	@Transactional(readOnly = false)
	@Cacheable(value= "filmCache")
	public List<Film> findAllFilms() {
		return filmDao.findAllFilms();
	}
	@CacheEvict(value= "filmCache", allEntries = true)
	@Transactional(readOnly = false)
	public void cleanAllFilms() {
		filmDao.cleanAllFilms();
	}
	@Transactional(readOnly = true)
	public List<Film> getAllRippedFilms(){
		return filmDao.getAllRippedFilms();
	}
	@Override
	public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto) {
		List<Film> filmList = filmDao.findAllFilmsByCriteria(filmFilterCriteriaDto);
		filmList.sort(Comparator.comparing(Film::getPrintRealisateur).thenComparing(Film::getTitre));
		return filmList;
	}
	@Override
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
	public Set<Long> findAllTmdbFilms(Set<Long> tmdbIds) {
		return filmDao.findAllTmdbFilms(tmdbIds);
	}
	
	
	/** TEST PURPOSE **/
	private Dvd buildDvd(Integer annee) {
		Dvd dvd = new Dvd();
		dvd.setAnnee(annee);
		dvd.setEdition("edition");
		dvd.setZone(1);
		return dvd;
	}
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
			final Personne act3) {
		Film film = new Film();
		film.setAnnee(annee);
		film.setRipped(true);
		film.setTitre(titre);
		film.setTitreO(titre);
		film.setDvd(buildDvd(annee));
		film.setRealisateurs(buildRealisateurs(realisateur));
		film.setActeurs(buildActeurs(act1,act2,act3));
		film.setTmdbId(new Long(100));
		return film;
	}
	@Override
	public Film createOrRetrieveFilm(final String titre,final Integer annee,
			final String realNom,
			final String act1Nom,
			final String act2Nom,
			final String act3Nom) {
		return createFilm(titre,annee, realNom, act1Nom, act2Nom, act3Nom);
	}
	private Film createFilm(final String titre,
			final Integer annee,
			final String realNom,
			final String act1Nom,
			final String act2Nom,
			final String act3Nom) {
		Personne realisateur = null;
		Personne acteur1 = null;
		Personne acteur2 = null;
		Personne acteur3 = null;
		Integer idRealisateur = createPersonne(realNom);
		realisateur = personneService.findByPersonneId(idRealisateur);
		Integer idActeur1 = createPersonne(act1Nom);
		acteur1 = personneService.findByPersonneId(idActeur1);
		Integer idActeur2 = createPersonne(act2Nom);
		acteur2 = personneService.findByPersonneId(idActeur2);
		Integer idActeur3 = createPersonne(act3Nom);
		acteur3 = personneService.findByPersonneId(idActeur3);
		Film film = buildFilm(titre,annee,realisateur,acteur1,acteur2,acteur3);
		Integer idFilm = saveNewFilm(film);
		film.setId(idFilm);
		return film;
	}
	
	private Integer createPersonne(final String nom) {
		return personneService.savePersonne(personneService.buildPersonne(nom));
	}
	/** TEST PURPOSE **/
}
