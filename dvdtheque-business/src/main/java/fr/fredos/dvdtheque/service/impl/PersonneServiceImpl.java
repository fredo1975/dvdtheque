package fr.fredos.dvdtheque.service.impl;

import java.util.ArrayList;
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
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.dto.PersonnesFilm;
import fr.fredos.dvdtheque.dto.RealisateurDto;
import fr.fredos.dvdtheque.service.PersonneService;
@Service("personneService")
public class PersonneServiceImpl implements PersonneService {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceImpl.class);
	public static final String CACHE_REPL_PERSONNE = "repl-personne";
	
	@Autowired
	private PersonneDao personneDao;
	@Transactional(readOnly = true)
	public PersonneDto findByPersonneId(Integer id){
		String methodName = "findByPersonneId : ";
		logger.debug(methodName + "start");
		PersonneDto personneDto=null;
		Personne personne;
		personne = personneDao.findByPersonneId(id);
		if(personne!=null) {
			personneDto = PersonneDto.toDto(personne);
		}
		if (null != personneDto) {
			logger.debug(methodName + "end personneDto=" + personneDto.toString());
		}
		return personneDto;
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
	public RealisateurDto findRealisateurByFilm(FilmDto filmDto) {
		String methodName = "findRealisateurByFilm : ";
		logger.debug(methodName + "start");
		RealisateurDto realisateurDto = null;
		Personne realisateur = null;
		Film film = filmDto.fromDto();
		realisateur = personneDao.findRealisateurByFilm(film);
		realisateurDto = RealisateurDto.toDto(realisateur);
		if (null != realisateurDto) {
			logger.debug(methodName + "end realisateurDto=" + realisateurDto.toString());
		}
		logger.debug(methodName + "end");
		return realisateurDto;
	}
	@Cacheable(value= "personneDtoCache")
	@Transactional(readOnly = true)
	public List<PersonneDto> findAllPersonne(){
		String methodName = "findAllPersonne : ";
		logger.debug(methodName + "start : ");
		List<PersonneDto> personneDtoList = new ArrayList<>();
		List<Personne> allPersonneList = personneDao.findAllPersonne();
		for(Personne p : allPersonneList){
			personneDtoList.add(PersonneDto.toDto(p));
		}
		logger.debug(methodName + "end");
		return personneDtoList;
	}
	
	@Transactional(readOnly = true)
	public PersonnesFilm findAllPersonneByFilm(FilmDto film){
		String methodName = "findAllPersonneByFilm : ";
		logger.debug(methodName + "start : ");
		PersonnesFilm personnesFilm = new PersonnesFilm();
		List<PersonneDto> personneDtoList = new ArrayList<PersonneDto>();
		List<Personne> allPersonneList = personneDao.findAllPersonneByFilm(film.fromDto());
		for(Personne personne : allPersonneList){
			PersonneDto personneDto = PersonneDto.toDto(personne);
			personneDtoList.add(personneDto);
		}
		Personne real = personneDao.findRealisateurByFilm(film.fromDto());
		personnesFilm.setRealisateur(RealisateurDto.toDto(real));
		logger.debug(methodName + "end");
		return personnesFilm;
	}
	@Transactional(readOnly = false)
	public Personne savePersonne(Personne personne) {
		return personneDao.mergePersonne(personne);
	}
	@Transactional(readOnly = false)
	public PersonneDto savePersonne(PersonneDto personneDto) {
		String methodName = "savePersonne : ";
		logger.debug(methodName + "start ");
		PersonneDto resultPersonneDto = null;
		if(null != personneDto){
			Personne p = PersonneDto.fromDto(personneDto);
			//p.setId(personneDao.savePersonne(p));
			Personne pPersisted = personneDao.mergePersonne(p);
			resultPersonneDto = PersonneDto.toDto(pPersisted);
		}else{
			String msg = "personneDto must be not null";
			throw new IllegalArgumentException(msg);
		}
		
		logger.debug(methodName + "end");
		return resultPersonneDto;
	}
	@CacheEvict(value= "personneDtoCache")
	@Transactional(readOnly = false)
	public PersonneDto updatePersonne(PersonneDto personneDto){
		String methodName = "updatePersonne : ";
		logger.debug(methodName + "start : personneDto="+personneDto.toString());
		PersonneDto resultPersonneDto = null;
		if(null != personneDto){
			Personne p = personneDao.findByPersonneId(personneDto.getId());
			p.setNom(personneDto.getNom());
			p.setPrenom(personneDto.getPrenom());
			personneDao.updatePersonne(p);
			resultPersonneDto = PersonneDto.toDto(p);
		}
		logger.debug(methodName + "end");
		return resultPersonneDto;
	}
	@CacheEvict(value= "personneDtoCache")
	@Transactional(readOnly = false)
	public void deletePersonne(PersonneDto personneDto){
		String methodName = "deletePersonne : ";
		logger.debug(methodName + "start : personneDto="+personneDto.toString());
		Personne p = PersonneDto.fromDto(personneDto);
		personneDao.deletePersonne(p);
		logger.debug(methodName + "end");
	}
	@Transactional(readOnly = true)
	public PersonneDto findPersonneByFullName(String nom,String prenom){
		String methodName = "findPersonneByFullName : ";
		logger.debug(methodName + "start : nom="+nom+" prenom="+prenom);
		Personne p = null;
		PersonneDto pDto = null;
		p = personneDao.findPersonneByFullName(nom, prenom);
		if(null != p){
			pDto = PersonneDto.toDto(p);
		}else{
			
		}
		logger.debug(methodName + "end");
		return pDto;
	}
	@Transactional(readOnly = true)
	public PersonneDto findPersonneByName(String nom){
		String methodName = "findPersonneByName : ";
		logger.debug(methodName + "start : nom="+nom);
		Personne p = null;
		PersonneDto pDto = null;
		p = personneDao.findPersonneByName(nom);
		if(null != p){
			pDto = PersonneDto.toDto(p);
		}else{
			
		}
		logger.debug(methodName + "end");
		return pDto;
	}
	@Override
	public void cleanAllPersonnes() {
		personneDao.cleanAllPersons();
	}
	
	@Override
	public List<PersonneDto> findAllRealisateur() {
		List<PersonneDto> personneDtoList = new ArrayList<>();
		List<Personne> allRealList = personneDao.findAllRealisateur();
		for(Personne p : allRealList){
			personneDtoList.add(PersonneDto.toDto(p));
		}
		return personneDtoList;
	}
	@Override
	public List<PersonneDto> findAllActeur() {
		List<PersonneDto> personneDtoList = new ArrayList<>();
		List<Personne> allRealList = personneDao.findAllRealisateur();
		for(Personne p : allRealList){
			personneDtoList.add(PersonneDto.toDto(p));
		}
		return personneDtoList;
	}
}
