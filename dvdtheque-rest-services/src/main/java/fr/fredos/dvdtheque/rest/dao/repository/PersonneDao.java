package fr.fredos.dvdtheque.rest.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.rest.dao.domain.Personne;
@Repository("personneDao")
public interface PersonneDao extends JpaRepository<Personne, Long>, JpaSpecificationExecutor<Personne>{
	Personne findPersonneByNom(String nom);
}
