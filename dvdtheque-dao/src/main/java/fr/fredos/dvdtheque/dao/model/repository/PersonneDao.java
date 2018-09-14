package fr.fredos.dvdtheque.dao.model.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
@Repository
public class PersonneDao {
	protected Logger logger = LoggerFactory.getLogger(PersonneDao.class);
	@PersistenceContext
    private EntityManager em;
	public Personne findByPersonneId(Integer id)  {
		Personne p = this.em.find(Personne.class, id);
		return p;
	}
	public Personne getPersonne(Integer id)  {
		Personne p = this.em.find(Personne.class, id);
		return p;
	}
	public Personne loadPersonne(Integer id)  {
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
	@SuppressWarnings("unchecked")
    public List<Personne> findAllPersonne() {
		Query q = this.em.createQuery("from Personne personne order by personne.prenom,personne.nom ASC");
		return q.getResultList();
    }
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	public Personne findPersonneByFullName(String nom,String prenom) {
		StringBuilder sb = new StringBuilder("from Personne personne where personne.nom=:nom and personne.prenom=:prenom ");
        Query query = this.em.createQuery(sb.toString());
        query.setParameter("nom", nom);
        query.setParameter("prenom", prenom);
        return (Personne) query.getSingleResult();
    }
	@SuppressWarnings("unchecked")
	public Personne findPersonneByName(String nom) {
		StringBuilder sb = new StringBuilder("from Personne personne where personne.nom=:nom ");
        Query query = this.em.createQuery(sb.toString());
        query.setParameter("nom", nom);
        return (Personne) query.getSingleResult();
    }
	
	@SuppressWarnings("unchecked")
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
	public void savePersonne(Personne p){
		this.em.persist(p);
	}
	public Personne mergePersonne(Personne p){
		return (Personne) this.em.merge(p);
	}
	public void updatePersonne(Personne p){
		this.em.merge(p);
	}
	public void deletePersonne(Personne p){
		this.em.remove(p);
	}
	public void cleanAllPersons() {
		/*
		Query queryActeur = this.em.createQuery("delete from Acteur");
		int nbActeur = queryActeur.executeUpdate();
		logger.info(nbActeur+" acteur deleted");
		Query queryRealisateur = this.em.createQuery("delete from Acteur");
		int nbRealisateur = queryRealisateur.executeUpdate();
		logger.info(nbRealisateur+" realisateur deleted");*/
		Query queryPersonne = this.em.createQuery("delete from Personne");
		int nbPersonne = queryPersonne.executeUpdate();
		logger.info(nbPersonne+" personne deleted");
	}
}
