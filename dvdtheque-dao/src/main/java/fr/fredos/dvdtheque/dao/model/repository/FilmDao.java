package fr.fredos.dvdtheque.dao.model.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.FilmFilterCriteriaType;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.RippedFilm;

@Repository
public class FilmDao {
	protected Logger logger = LoggerFactory.getLogger(FilmDao.class);
	@PersistenceContext
    private EntityManager em;

	public Film findFilm(Integer id){
		Query q = this.em.createQuery("from Film film join fetch film.realisateurs real where film.id = :id");
		q.setParameter("id", id);
		return (Film)q.getSingleResult();
	}
	public RippedFilm findRippedFilm(Integer id){
		Query q = this.em.createQuery("from RippedFilm film where film.id = :id");
		q.setParameter("id", id);
		return (RippedFilm)q.getSingleResult();
	}
	public Film findFilmByTitre(String titre){
		Query q = this.em.createQuery("from Film film where film.titre = :titre");
		q.setParameter("titre", titre);
		return (Film)q.getSingleResult();
	}
	public RippedFilm findRippedFilmByTitre(String titre){
		Query q = this.em.createQuery("from RippedFilm film where film.titre = :titre");
		q.setParameter("titre", titre);
		return (RippedFilm)q.getSingleResult();
	}
	public Film findFilmWithAllObjectGraph(Integer id){
		Query q = this.em.createQuery("select f from Film f left join f.acteurs acteur where f.id = :id ");
		q.setParameter("id", id);
		return (Film)q.getSingleResult();
	}
	public void saveNewFilm(Film film){
		this.em.persist(film);
		this.em.flush();
	}
	public void saveNewRippedFilm(RippedFilm film){
		this.em.persist(film);
		this.em.flush();
	}
	public Film updateFilm(Film film){
		Film filmResult = this.em.merge(film);
		this.em.flush();
		return filmResult;
	}
	public void saveDvd(Dvd dvd){
		this.em.persist(dvd);
	}
	@SuppressWarnings("unchecked")
    public List<Film> findAllFilms() {
		Query query = this.em.createQuery("from Film film left join fetch film.acteurs left join fetch film.realisateurs real ");
        return new ArrayList<Film>(new HashSet<Film>(query.getResultList()));
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
		Query query = this.em.createQuery(sb.toString());
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
		Query queryDvd = this.em.createQuery("delete from Dvd");
		int nbDvd = queryDvd.executeUpdate();
		logger.debug(nbDvd+" dvd deleted");
		Query query = this.em.createQuery("delete from Film");
		int nb = query.executeUpdate();
		logger.debug(nb+" films deleted");
	}
	public void cleanAllRippedFilms() {
		Query query = this.em.createQuery("delete from RippedFilm");
		int nb = query.executeUpdate();
		logger.debug(nb+" films deleted");
	}
	public List<Film> getAllRippedFilms(){
		Query query = this.em.createQuery("from Film film where film.ripped=1");
        return query.getResultList();
	}
	
	public void removeFilm(Film film) {
		this.em.remove(film);
		this.em.flush();
	}
}
