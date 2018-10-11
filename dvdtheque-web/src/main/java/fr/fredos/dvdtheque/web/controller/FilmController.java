package fr.fredos.dvdtheque.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.dto.FilmDto;

@RestController
@ComponentScan({"fr.fredos.dvdtheque.service,fr.fredos.dvdtheque.dao.model.repository"})
@RequestMapping("/dvdtheque")
public class FilmController {
	@Autowired
	private FilmService filmService;
	
	@GetMapping("/films")
	List<FilmDto> findAllFilms() {
		return filmService.getAllFilmDtos();
	}
	
	@GetMapping("/films/{titre}")
	FilmDto findFilmByTitre(@PathVariable String titre) {
		return filmService.findFilmByTitre(titre);
	}
}
