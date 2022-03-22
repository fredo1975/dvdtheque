package fr.fredos.dvdtheque.rest.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import fr.fredos.dvdtheque.rest.dao.repository.PersonneDao;
import fr.fredos.dvdtheque.rest.service.IPersonneService;

@Service("personneService")
public class PersonneServiceImpl implements IPersonneService {
	protected Logger logger = LoggerFactory.getLogger(PersonneServiceImpl.class);
	public static final String CACHE_PERSONNE = "personneCache";
	private final PersonneDao personneDao;
	IMap<Long, Personne> mapPersonnes;
	private final HazelcastInstance instance;

	public PersonneServiceImpl(PersonneDao personneDao, HazelcastInstance instance) {
		this.personneDao = personneDao;
		this.instance = instance;
		this.init();
	}

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
		Optional<Personne> personneOpt = personneDao.findById(id);
		if(personneOpt.isEmpty()) {
			return null;
		}
		personne = personneOpt.get();
		mapPersonnes.put(id, personne);
		return personne;
	}

	@Transactional(readOnly = true)
	public Personne getPersonne(Long id) {
		Personne personne = mapPersonnes.get(id);
		if (personne != null)
			return personne;
		Optional<Personne> personneOpt = personneDao.findById(id);
		if(personneOpt.isEmpty()) {
			return null;
		}
		personne = personneOpt.get();
		mapPersonnes.put(id, personne);
		return personne;
	}

	@Transactional(readOnly = true)
	public Personne loadPersonne(Long id) {
		Personne personne = mapPersonnes.get(id);
		if (personne != null)
			return personne;
		Optional<Personne> personneOpt = personneDao.findById(id);
		if(personneOpt.isEmpty()) {
			return null;
		}
		personne = personneOpt.get();
		mapPersonnes.put(id, personne);
		return personne;
	}


	private List<Personne> sortPersonneList(Collection<Personne> personnes) {
		if (personnes.size() > 0) {
			List<Personne> l = personnes.stream().collect(Collectors.toList());
			Collections.sort(l, (f1,f2)->f1.getNom().compareTo(f2.getNom()));
			return l;
		}
		return new ArrayList<>();
	}
	// @Cacheable(value= CACHE_PERSONNE)
	@Transactional(readOnly = true)
	public List<Personne> findAllPersonne() {
		Collection<Personne> personnes = mapPersonnes.values();
		logger.info("personnes cache size: " + personnes.size());
		if (personnes.size() > 0) {
			List<Personne> l = sortPersonneList(personnes);
			return l;
		}
		logger.info("personnes find");
		List<Personne> personnesList = this.personneDao.findAll();
		List<Personne> l = new ArrayList<>();
		if (personnesList.size() > 0) {
			l.addAll(sortPersonneList(personnesList));
		}
		logger.info("personnesList size: " + l.size());
		l.parallelStream().forEach(it -> {
			mapPersonnes.putIfAbsent(it.getId(), it);
		});
		return l;
	}

	// @CacheEvict(value= CACHE_PERSONNE, allEntries = true)
	@Transactional(readOnly = false)
	public Long savePersonne(Personne personne) {
		Personne savedPersonne = personneDao.save(personne);
		mapPersonnes.put(savedPersonne.getId(), savedPersonne);
		return savedPersonne.getId();
	}

	// @CacheEvict(value= CACHE_PERSONNE, allEntries = true)
	@Transactional(readOnly = false)
	public void updatePersonne(Personne personne) {
		if (null != personne) {
			personneDao.save(personne);
			mapPersonnes.put(personne.getId(), personne);
		}
	}

	@Transactional(readOnly = true)
	public Personne attachSessionPersonneByName(String nom) {
		return personneDao.findPersonneByNom(nom);
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
		Personne personne = personneDao.findPersonneByNom(nom);
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
		personneDao.deleteAll();
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
