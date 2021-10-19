package fr.fredos.dvdtheque.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.repository.PersonneDao;
import fr.fredos.dvdtheque.service.IPersonneService;

@Service("personneService")
public class PersonneServiceImpl implements IPersonneService {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceImpl.class);
	private static final String CACHE_PERSONNE = "personneCache";
	private final PersonneDao personneDao;
	IMap<Long, Personne> mapPersonnes;
	private final HazelcastInstance instance;

	public PersonneServiceImpl(PersonneDao personneDao, HazelcastInstance instance) {
		this.personneDao = personneDao;
		this.instance = instance;
	}

	@PostConstruct
	public void init() {
		mapPersonnes = instance.getMap(CACHE_PERSONNE);
		/*mapPersonnes.addIndex("id", true);
		mapPersonnes.addIndex("nom", false);*/
	}

	@Override
	public void cleanAllCaches() {
		mapPersonnes.clear();
	}

	@Transactional(readOnly = true)
	public Personne findByPersonneId(Long id) {
		Personne personne = mapPersonnes.get(id);
		if (personne != null)
			return personne;
		personne = personneDao.findByPersonneId(id);
		mapPersonnes.put(id, personne);
		return personne;
	}

	@Transactional(readOnly = true)
	public Personne getPersonne(Long id) {
		Personne personne = mapPersonnes.get(id);
		if (personne != null)
			return personne;
		personne = personneDao.getPersonne(id);
		mapPersonnes.put(id, personne);
		return personne;
	}

	@Transactional(readOnly = true)
	public Personne loadPersonne(Long id) {
		Personne personne = mapPersonnes.get(id);
		if (personne != null)
			return personne;
		personne = personneDao.loadPersonne(id);
		mapPersonnes.put(id, personne);
		return personne;
	}

	@Transactional(readOnly = true)
	public Personne findRealisateurByFilm(Film film) {
		Personne realisateur = null;
		realisateur = personneDao.findRealisateurByFilm(film);
		return realisateur;
	}

	// @Cacheable(value= CACHE_PERSONNE)
	@Transactional(readOnly = true)
	public List<Personne> findAllPersonne() {
		Collection<Personne> personnes = mapPersonnes.values();
		logger.info("personnes cache size: " + personnes.size());
		if (personnes.size() > 0) {
			List<Personne> l = personnes.stream().collect(Collectors.toList());
			Collections.sort(l, (f1,f2)->f1.getNom().compareTo(f2.getNom()));
			return l;
		}
		logger.info("personnes find");
		List<Personne> personnesList = this.personneDao.findAllPersonne();
		logger.info("personnesList size: " + personnesList.size());
		personnesList.parallelStream().forEach(it -> {
			mapPersonnes.putIfAbsent(it.getId(), it);
		});
		return personnesList;
	}

	// @CacheEvict(value= CACHE_PERSONNE, allEntries = true)
	@Transactional(readOnly = false)
	public Long savePersonne(Personne personne) {
		Long id = personneDao.savePersonne(personne);
		mapPersonnes.put(id, personne);
		return id;
	}

	// @CacheEvict(value= CACHE_PERSONNE, allEntries = true)
	@Transactional(readOnly = false)
	public void updatePersonne(Personne personne) {
		if (null != personne) {
			personneDao.updatePersonne(personne);
			mapPersonnes.put(personne.getId(), personne);
		}
	}

	@Transactional(readOnly = true)
	public Personne attachSessionPersonneByName(String nom) {
		return personneDao.findPersonneByName(nom);
	}
	@Transactional(readOnly = true)
	public Personne findPersonneByName(String nom) {
		Predicate<Long,Personne> predicate = Predicates.equal("nom", nom);
		logger.info("personne cache find");
		Collection<Personne> ps = mapPersonnes.values(predicate);
		logger.info("Personne cached: " + ps);
		Optional<Personne> e = ps.stream().findFirst();
		if (e.isPresent())
			return e.get();
		logger.info("no Personne cache find");
		Personne personne = personneDao.findPersonneByName(nom);
		if(personne == null) {
			return null;
		}
		logger.info("personne: " + personne);
		mapPersonnes.put(personne.getId(), personne);
		return personne;
	}

	@Override
	@Transactional(readOnly = false)
	public void cleanAllPersonnes() {
		mapPersonnes.clear();
		personneDao.cleanAllPersons();
	}

	@Override
	public void deletePersonne(Personne p) {
		// TODO Auto-generated method stub
	}

	@Override
	public Personne buildPersonne(String nom, String profilePath) {
		Personne personne = new Personne();
		personne.setNom(nom);
		if (StringUtils.isNotEmpty(profilePath)) {
			personne.setProfilePath(profilePath);
		}
		return personne;
	}

	@Override
	@Transactional(readOnly = false)
	// @Cacheable(value= CACHE_PERSONNE)
	public Personne createOrRetrievePersonne(String nom, String profilePath) {
		Personne p = attachSessionPersonneByName(nom);
		if (p == null) {
			p = buildPersonne(nom, profilePath);
			Long id = savePersonne(p);
			p.setId(id);
		}
		return p;
	}

	@Override
	public String printPersonnes(final Set<Personne> personnes, final String separator) {
		if (CollectionUtils.isNotEmpty(personnes)) {
			StringBuilder sb = new StringBuilder();
			personnes.forEach(real -> {
				sb.append(real.getNom()).append(separator);
			});
			return StringUtils.chomp(sb.toString(), separator);
		}
		return StringUtils.EMPTY;
	}
}
