package fr.fredos.dvdtheque.web.controller;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;

@RestController
@ComponentScan({"fr.fredos.dvdtheque.service,fr.fredos.dvdtheque.dao.model.repository"})
@RequestMapping("/dvdtheque")
public class FilmController {
	protected Logger logger = LoggerFactory.getLogger(FilmController.class);
	@Autowired
	private FilmService filmService;
	@Autowired
	protected PersonneService personneService;
	
	@CrossOrigin
	@GetMapping("/films/byPersonne")
	Personne findPersonne(@RequestParam(name="nom",required = false) String nom,@RequestParam(name="prenom",required = false) String prenom) {
		return personneService.findPersonneByFullName(nom, prenom);
	}
	@CrossOrigin
	@GetMapping("/films")
	List<Film> findAllFilms() {
		return filmService.findAllFilms();
	}
	@CrossOrigin
	@GetMapping("/films/byTitre/{titre}")
	Film findFilmByTitre(@PathVariable String titre) {
		return filmService.findFilmByTitre(titre);
	}
	@CrossOrigin
	@GetMapping("/films/byId/{id}")
	Film findFilmById(@PathVariable Integer id) {
		return filmService.findFilm(id);
	}
	@CrossOrigin
	@GetMapping("/realisateurs")
	List<Personne> findAllRealisateurs() {
		return personneService.findAllRealisateur();
	}
	@CrossOrigin
	@GetMapping("/acteurs")
	List<Personne> findAllActeurs() {
		return personneService.findAllActeur();
	}
	@CrossOrigin
	@GetMapping("/personnes")
	List<Personne> findAllPersonne() {
		return personneService.findAllPersonne();
	}
	
	@CrossOrigin
	@PutMapping("/films/{id}")
	ResponseEntity<Object> updateFilm(@RequestBody Film film,@PathVariable Integer id) {
		Film filmOptional = filmService.findFilm(id);

		if(filmOptional==null) {
			return ResponseEntity.notFound().build();
		}
		filmService.updateFilm(film);
		return ResponseEntity.noContent().build();
	}
	@CrossOrigin
	@PutMapping("/personnes/byId/{id}")
	ResponseEntity<Object> updatePersonne(@RequestBody Personne p,@PathVariable Integer id) {
		Personne personne = personneService.findByPersonneId(id);
		if(personne==null) {
			return ResponseEntity.notFound().build();
		}
		if(StringUtils.isNotEmpty(p.getPrenom())) {
			personne.setPrenom(StringUtils.upperCase(p.getPrenom()));
		}
		if(StringUtils.isNotEmpty(p.getNom())) {
			personne.setNom(StringUtils.upperCase(p.getNom()));
		}
		personneService.updatePersonne(personne);
		logger.info(personne.toString());
		return ResponseEntity.noContent().build();
	}
	@CrossOrigin
	@PostMapping("/films")
	ResponseEntity<Object> saveFilm(@RequestBody Film film) {
		Integer id = filmService.saveNewFilm(film);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}
}
