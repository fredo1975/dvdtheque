package fr.fredos.dvdtheque.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.service.FilmService;

@RestController
@ComponentScan({ "fr.fredos.dvdtheque.service,fr.fredos.dvdtheque.dao.model.repository"})
public class FilmController {
	@Autowired
	private FilmService filmService;
	
	@GetMapping("/films")
	List<FilmDto> all() {
		return filmService.getAllFilmDtos();
	}
	
	@GetMapping("/films/{titre}")
	FilmDto one(@PathVariable String titre) {
		return filmService.findFilmByTitre(titre);
	}
}
