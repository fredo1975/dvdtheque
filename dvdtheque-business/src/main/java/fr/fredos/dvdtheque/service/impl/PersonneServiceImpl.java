package fr.fredos.dvdtheque.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.PersonneDao;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
@Service("personneService")
public class PersonneServiceImpl implements IPersonneService {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceImpl.class);
	public static final String CACHE_REPL_PERSONNE = "repl-personne";
	
	@Autowired
	private PersonneDao personneDao;
	@Transactional(readOnly = true)
	public Personne findByPersonneId(Integer id){
		return personneDao.findByPersonneId(id);
	}
	@Transactional(readOnly = true)
	public Personne getPersonne(Integer id){
		Personne personne = personneDao.getPersonne(id);
		return personne;
	}
	@Transactional(readOnly = true)
	public Personne loadPersonne(Integer id){
		Personne personne = personneDao.loadPersonne(id);
		return personne;
	}
	@Transactional(readOnly = true)
	public Personne findRealisateurByFilm(Film film) {
		Personne realisateur = null;
		realisateur = personneDao.findRealisateurByFilm(film);
		return realisateur;
	}
	@Cacheable(value= "personneCache")
	@Transactional(readOnly = true)
	public List<Personne> findAllPersonne(){
		return personneDao.findAllPersonne();
	}
	
	@CacheEvict(value= "personneCache", allEntries = true)
	@Transactional(readOnly = false)
	public Integer savePersonne(Personne personne) {
		return personneDao.savePersonne(personne);
	}
	
	@CacheEvict(value= "personneCache", allEntries = true)
	@Transactional(readOnly = false)
	public void updatePersonne(Personne personne){
		if(null != personne){
			Personne p = personneDao.findByPersonneId(personne.getId());
			personneDao.updatePersonne(p);
		}
	}
	@CacheEvict(value= "personneDtoCache", allEntries = true)
	@Transactional(readOnly = false)
	public void deletePersonne(PersonneDto personneDto){
		String methodName = "deletePersonne : ";
		logger.debug(methodName + "start : personneDto="+personneDto.toString());
		Personne p = PersonneDto.fromDto(personneDto);
		personneDao.deletePersonne(p);
		logger.debug(methodName + "end");
	}
	@Transactional(readOnly = true)
	public Personne findPersonneByFullName(String nom){
		return personneDao.findPersonneByFullName(nom);
	}
	@Transactional(readOnly = true)
	public Personne findPersonneByName(String nom){
		return personneDao.findPersonneByName(nom);
	}
	@Override
	@Transactional(readOnly = false)
	public void cleanAllPersonnes() {
		personneDao.cleanAllPersons();
	}
	@Override
	public List<Personne> findAllRealisateur() {
		return personneDao.findAllRealisateur();
	}
	@Override
	public List<Personne> findAllActeur() {
		return personneDao.findAllActeur();
	}
	@Override
	public void deletePersonne(Personne p) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public Personne buildPersonne(String nom) {
		Personne personne = new Personne();
		personne.setNom(nom);
		return personne;
	}
}
