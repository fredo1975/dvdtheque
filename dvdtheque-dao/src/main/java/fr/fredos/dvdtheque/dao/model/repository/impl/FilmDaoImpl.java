package fr.fredos.dvdtheque.dao.model.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.FilmFilterCriteriaType;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.repository.FilmDao;
@Repository("filmDao")
public class FilmDaoImpl implements FilmDao {
	protected final Log logger = LogFactory.getLog(getClass());
	@PersistenceContext
    private EntityManager entityManager;
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	public Film findFilm(Integer id){
		Query q = this.entityManager.createQuery("from Film film join fetch film.realisateurs real where film.id = :id");
		q.setParameter("id", id);
		return (Film)q.getSingleResult();
	}
	public Film findFilmByTitre(String titre){
		Query q = this.entityManager.createQuery("from Film film where film.titre = :titre");
		q.setParameter("titre", titre);
		return (Film)q.getSingleResult();
	}
	public Film findFilmWithAllObjectGraph(Integer id){
		Query q = this.entityManager.createQuery("select f from Film f left join f.acteurs acteur where f.id = :id ");
		q.setParameter("id", id);
		return (Film)q.getSingleResult();
	}
	public Integer saveNewFilm(Film film){
		this.entityManager.persist(film);
		this.entityManager.flush();
		return film.getId();
	}
	public void updateFilm(Film film){
		this.entityManager.merge(film);
		this.entityManager.flush();
		//return filmResult;
	}
	public Integer saveDvd(Dvd dvd){
		this.entityManager.persist(dvd);
		return dvd.getId();
	}
	@SuppressWarnings("unchecked")
    public List<Film> findAllFilms() {
		Query query = this.entityManager.createQuery("from Film film left join fetch film.acteurs left join fetch film.realisateurs real ");
        Set<Film> set = new HashSet<>(query.getResultList());
        List<Film> l = new ArrayList<>(set);
        Collections.sort(l, (f1,f2)->f1.getTitre().compareTo(f2.getTitre()));
		return l;
    }
	@SuppressWarnings("unchecked")
    public List<Film> findAllFilmsByCriteria(FilmFilterCriteriaDto filmFilterCriteriaDto) {
		StringBuilder sb = new StringBuilder("from Film film left join fetch film.acteurs act left join fetch film.realisateurs real ");
		sb.append("left join fetch film.acteurs act2 ");
		if(filmFilterCriteriaDto!=null) {
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
		}
		return new ArrayList<Film>(new HashSet<Film>(query.getResultList()));
    }
	public void cleanAllFilms() {
		Query queryDvd = this.entityManager.createQuery("delete from Dvd");
		int nbDvd = queryDvd.executeUpdate();
		logger.info(nbDvd+" dvd deleted");
		Query query = this.entityManager.createQuery("delete from Film");
		int nb = query.executeUpdate();
		logger.info(nb+" films deleted");
	}
	public List<Film> getAllRippedFilms(){
		Query query = this.entityManager.createQuery("from Film film where film.ripped=1");
        return query.getResultList();
	}
	
	public void removeFilm(Film film) {
		this.entityManager.remove(film);
		this.entityManager.flush();
	}
}
