package fr.fredos.dvdtheque.dao.model.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.FilmFilterCriteriaType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
@Repository("filmDao")
public class FilmDaoImpl implements FilmDao {
	protected final Log logger = LogFactory.getLog(getClass());
	@PersistenceContext
    private EntityManager entityManager;
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	public Genre findGenre(int tmdbId){
		Query q = this.entityManager.createQuery("from Genre g where g.tmdbId = :tmdbId");
		q.setParameter("tmdbId", tmdbId);
		Genre g = null;
		try {
        	g = (Genre)q.getSingleResult();
        }catch(NoResultException nre) {
        	logger.debug("genre tmdbId="+tmdbId+" not found");
        }
		return g;
	}
	public List<Genre> findAllGenres() {
		Query query = this.entityManager.createQuery("from Genre order by name asc");
        return (List<Genre>)query.getResultList();
	}
	public Genre saveGenre(Genre genre){
		this.entityManager.persist(genre);
		this.entityManager.flush();
		return genre;
	}
	public void removeGenres() {
		
	}
	public Film findFilm(Long id){
		Query q = this.entityManager.createQuery("from Film film join fetch film.realisateurs real where film.id = :id");
		q.setParameter("id", id);
		return (Film)q.getSingleResult();
	}
	public FilmOrigine findFilmOrigine(Long id){
		Query q = this.entityManager.createQuery("select origine from Film film where film.id = :id");
		q.setParameter("id", id);
		return (FilmOrigine)q.getSingleResult();
	}
	public Film findFilmByTitre(String titre){
		Query q = this.entityManager.createQuery("from Film where UPPER(titre) = UPPER(:titre)");
		q.setParameter("titre", titre);
		try {
			return (Film)q.getSingleResult();
		}catch(NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(NonUniqueResultException nre) {
			logger.error(nre.getMessage());
		}
		return null;
	}
	public Film findFilmWithAllObjectGraph(Long id){
		Query q = this.entityManager.createQuery("from Film where id = :id ");
		q.setParameter("id", id);
		return (Film)q.getSingleResult();
	}
	public Long saveNewFilm(Film film){
		this.entityManager.persist(film);
		return film.getId();
	}
	public Film updateFilm(Film film){
		return this.entityManager.merge(film);
	}
	public Long saveDvd(Dvd dvd){
		this.entityManager.persist(dvd);
		return dvd.getId();
	}
	@SuppressWarnings("unchecked")
    public List<Film> findAllFilms() {
		Query query = this.entityManager.createQuery("from Film film  ");
        Set<Film> set = new HashSet<>(query.getResultList());
        List<Film> l = new ArrayList<>(set);
        Collections.sort(l, (f1,f2)->f1.getTitre().compareTo(f2.getTitre()));
		return l;
    }
	@SuppressWarnings("unchecked")
    public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto) {
		StringBuilder sb = new StringBuilder("select film from Film film ");
		if(filmFilterCriteriaDto!=null) {
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.ACTEUR)
					&& filmFilterCriteriaDto.getSelectedActeur()!=null) {
				sb.append("join film.acteurs act ");
			}
			sb.append("where 1=1 ");
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.TITRE)
					&& StringUtils.isNotEmpty(filmFilterCriteriaDto.getTitre())) {
				sb.append("and UPPER(film.titre) LIKE CONCAT('%',UPPER(:titre),'%') ");
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.REALISATEUR)
					&& filmFilterCriteriaDto.getSelectedRealisateur()!=null) {
				sb.append("and real.id=:realId ");
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.ANNEE)
					&& filmFilterCriteriaDto.getAnnee()!=null) {
				sb.append("and film.annee=:annee ");
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.ACTEUR)
					&& filmFilterCriteriaDto.getSelectedActeur()!=null) {
				sb.append("and act.id=:actId ");
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.RIPPED)
					&& filmFilterCriteriaDto.getSelectedRipped()!=null) {
				sb.append("and film.ripped=:ripped");
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.ORIGINE)
					&& filmFilterCriteriaDto.getSelectedFilmOrigine()!=null) {
				sb.append("and film.origine=:origine");
			}
		}
		if(filmFilterCriteriaDto!=null 
				&& filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.RIPPED_SINCE)
				&& filmFilterCriteriaDto.getSelectedRippedSince()!=null) {
			sb.append("order by dvd.dateRip desc");
		}
		Query query = this.entityManager.createQuery(sb.toString());
		if(filmFilterCriteriaDto!=null) {
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.TITRE)
					&& StringUtils.isNotEmpty(filmFilterCriteriaDto.getTitre())) {
				query.setParameter("titre", filmFilterCriteriaDto.getTitre());
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.REALISATEUR)
					&& filmFilterCriteriaDto.getSelectedRealisateur()!=null) {
				query.setParameter("realId", filmFilterCriteriaDto.getSelectedRealisateur());
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.ANNEE)
					&& filmFilterCriteriaDto.getAnnee()!=null) {
				query.setParameter("annee", filmFilterCriteriaDto.getAnnee());
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.ACTEUR)
					&& filmFilterCriteriaDto.getSelectedActeur()!=null) {
				query.setParameter("actId", filmFilterCriteriaDto.getSelectedActeur());
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.RIPPED)
					&& filmFilterCriteriaDto.getSelectedRipped()!=null) {
				query.setParameter("ripped", filmFilterCriteriaDto.getSelectedRipped());
			}
			if(filmFilterCriteriaDto.getFilmFilterCriteriaTypeSet().contains(FilmFilterCriteriaType.ORIGINE)
					&& filmFilterCriteriaDto.getSelectedFilmOrigine()!=null) {
				query.setParameter("origine", filmFilterCriteriaDto.getSelectedFilmOrigine());
			}
		}
		List<Film> l = new ArrayList<Film>(new HashSet<Film>(query.getResultList()));
		//Collections.sort(l, (f1,f2)->f2.getDvd().getDateRip().compareTo(f1.getDvd().getDateRip()));
		return l;
    }
	public void cleanAllFilms() {
		for(Film film : findAllFilms()) {
			removeFilm(film);
		}
		this.entityManager.flush();
		
	}
	public void cleanAllGenres() {
		Query query = this.entityManager.createQuery("delete from Genre");
		int nbGenres = query.executeUpdate();
		logger.debug(nbGenres+" genres deleted");
		
	}
	public List<Film> getAllRippedFilms(){
		Query query = this.entityManager.createQuery("from Film film left join fetch film.dvd dvd where dvd.ripped=1");
        return query.getResultList();
	}
	public void removeFilm(Film film) {
		this.entityManager.remove(film);
		//this.entityManager.flush();
	}
	@Override
	public Set<Long> findAllTmdbFilms(Set<Long> tmdbIds) {
		StringBuilder sb = new StringBuilder("select film.tmdbId from Film film where film.tmdbId in (:tmdbIds) ");
		Query q = this.entityManager.createQuery(sb.toString());
		q.setParameter("tmdbIds", tmdbIds);
		return new HashSet<Long>(q.getResultList());
	}
	@Override
	public Boolean checkIfTmdbFilmExists(Long tmdbId) {
		StringBuilder sb = new StringBuilder("select count(1) from Film film where film.tmdbId = :tmdbId ");
		Query q = this.entityManager.createQuery(sb.toString());
		q.setParameter("tmdbId", tmdbId);
		return ((Long)q.getSingleResult())==1;
	}
	@Override
	public Genre attachToSession(Genre genre) {
		//Genre genre = findGenre(id);
		return this.entityManager.merge(genre);
	}
	@Override
	public List<Film> findAllFilmsByOrigine(FilmOrigine filmOrigine) {
		StringBuilder sb = new StringBuilder("from Film film where film.origine = :origine ");
		Query q = this.entityManager.createQuery(sb.toString());
		q.setParameter("origine", filmOrigine);
		return q.getResultList();
	}
}
