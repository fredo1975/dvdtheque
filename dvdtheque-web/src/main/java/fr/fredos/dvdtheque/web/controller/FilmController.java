package fr.fredos.dvdtheque.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;
import fr.fredos.dvdtheque.service.dto.FilmDto;
import fr.fredos.dvdtheque.service.dto.PersonneDto;

@RestController
@ComponentScan({"fr.fredos.dvdtheque.service,fr.fredos.dvdtheque.dao.model.repository"})
@RequestMapping("/dvdtheque")
public class FilmController {
	@Autowired
	private FilmService filmService;
	@Autowired
	protected PersonneService personneService;
	@CrossOrigin
	@GetMapping("/films")
	List<FilmDto> findAllFilms() {
		return filmService.getAllFilmDtos();
	}
	@CrossOrigin
	@GetMapping("/films/byTitre/{titre}")
	FilmDto findFilmByTitre(@PathVariable String titre) {
		return filmService.findFilmByTitre(titre);
	}
	@CrossOrigin
	@GetMapping("/films/byId/{id}")
	FilmDto findFilmById(@PathVariable Integer id) {
		return filmService.findFilm(id);
	}
	@CrossOrigin
	@GetMapping("/realisateurs")
	List<PersonneDto> findAllRealisateurs() {
		return personneService.findAllRealisateur();
	}
	@CrossOrigin
	@GetMapping("/acteurs")
	List<PersonneDto> findAllActeurs() {
		return personneService.findAllActeur();
	}
	
	@CrossOrigin
	@PutMapping("/films/byId/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	void updateFilm(@PathVariable Integer id,@RequestBody() FilmDto film) {
		filmService.updateFilm(film);
	}
}
