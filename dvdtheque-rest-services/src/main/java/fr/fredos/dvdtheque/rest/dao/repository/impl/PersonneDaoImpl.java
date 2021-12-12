package fr.fredos.dvdtheque.rest.dao.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import fr.fredos.dvdtheque.rest.dao.repository.PersonneDao;
@Repository("personneDao")
public class PersonneDaoImpl implements PersonneDao{
	protected Logger logger = LoggerFactory.getLogger(PersonneDao.class);
	@PersistenceContext
    private EntityManager em;
	public Personne findByPersonneId(Long id)  {
		Personne p = this.em.find(Personne.class, id);
		return p;
	}
	public Personne getPersonne(Long id)  {
		Personne p = this.em.find(Personne.class, id);
		return p;
	}
	public Personne loadPersonne(Long id)  {
		Personne p = this.em.find(Personne.class, id);
		return p;
	}
	public Personne findRealisateurByFilm(Film film)  {
		Query q = this.em.createQuery("from Film film join fetch film.realisateurs real where film = :film");
		q.setParameter("film", film);
		Film f = (Film) q.getSingleResult();
		if(!CollectionUtils.isEmpty(f.getRealisateurs())) {
			return f.getRealisateurs().iterator().next();
		}else
			return null;
		
	}
	public List<Personne> findAllRealisateur(){
		Query q = this.em.createQuery("from Film f join fetch f.realisateurs real");
		List<Film> filmList = q.getResultList();
		Set<Personne> set = filmList.stream().flatMap(list->list.getRealisateurs().stream()).collect(Collectors.toSet());
		List<Personne> l = new ArrayList<>(set);
		return l;
	}
	public List<Personne> findAllRealisateursByOrigine(FilmOrigine filmOrigine){
		Query q = this.em.createQuery("from Film f join fetch f.realisateurs real where f.origine=:origine");
		q.setParameter("origine", filmOrigine);
		List<Film> filmList = q.getResultList();
		Set<Personne> set = filmList.stream().flatMap(list->list.getRealisateurs().stream()).collect(Collectors.toSet());
		List<Personne> l = new ArrayList<>(set);
		return l;
	}
	public List<Personne> findAllActeur(){
		Query q = this.em.createQuery("from Film f join fetch f.acteurs act ");
		List<Film> filmList = q.getResultList();
		Set<Personne> set = filmList.stream().flatMap(list->list.getActeurs().stream()).collect(Collectors.toSet());
		List<Personne> l = new ArrayList<>(set);
		Collections.sort(l, (p1,p2)->(p1.getPrenom()+" "+p1.getNom()).compareTo((p2.getPrenom()+" "+p2.getNom())));
		return l;
	}
	public List<Personne> findAllActeursByOrigine(FilmOrigine filmOrigine){
		Query q = this.em.createQuery("from Film f join fetch f.acteurs act where f.origine=:origine");
		q.setParameter("origine", filmOrigine);
		List<Film> filmList = q.getResultList();
		Set<Personne> set = filmList.stream().flatMap(list->list.getActeurs().stream()).collect(Collectors.toSet());
		List<Personne> l = new ArrayList<>(set);
		Collections.sort(l, (p1,p2)->(p1.getPrenom()+" "+p1.getNom()).compareTo((p2.getPrenom()+" "+p2.getNom())));
		return l;
	}
    public List<Personne> findAllPersonne() {
		Query q = this.em.createQuery("from Personne personne order by personne.nom ASC");
		return q.getResultList();
    }
	public List<Personne> findAllPersonneByType(Integer typeId) {
		StringBuilder sb = new StringBuilder("select distinct personneType.personne ");
		if(typeId==2){
			sb.append("from Realisateur ");
		}else{
			sb.append("from Acteur ");
		}
		sb.append("personneType order by personneType.personne.prenom,personneType.personne.nom");
        Query query = this.em.createQuery(sb.toString());
        return query.getResultList();
    }
	public Personne findPersonneByName(String nom) {
		StringBuilder sb = new StringBuilder("from Personne personne where personne.nom=:nom ");
        Query query = this.em.createQuery(sb.toString());
        query.setParameter("nom", nom);
        Personne p = null;
        try {
        	p = (Personne) query.getSingleResult();
        }catch(NoResultException nre) {
        	logger.debug("personne name="+nom+" not found");
        }
        return p;
    }
    public List<Personne> findAllPersonneByFilm(Film film) {
		List<Personne> persList = new ArrayList<Personne>();
		StringBuilder sbRealisateur = new StringBuilder("from Film film join fetch film.realisateurs real join fetch film.acteurs act ");
		sbRealisateur.append("where film = :film ");
		Query queryRealisateur = this.em.createQuery(sbRealisateur.toString());
		queryRealisateur.setParameter("film", film);
		Film f = (Film) queryRealisateur.getSingleResult();
		persList.addAll(f.getActeurs());
		persList.addAll(f.getRealisateurs());
        return persList;
    }
	public Long savePersonne(Personne p){
		this.em.persist(p);
		this.em.flush();
		return p.getId();
	}
	public void updatePersonne(Personne p){
		this.em.merge(p);
	}
	public void deletePersonne(Personne p){
		this.em.remove(p);
	}
	public void cleanAllPersons() {
		Query queryPersonne = this.em.createQuery("delete from Personne");
		int nbPersonne = queryPersonne.executeUpdate();
		logger.debug(nbPersonne+" personne deleted");
	}
}
