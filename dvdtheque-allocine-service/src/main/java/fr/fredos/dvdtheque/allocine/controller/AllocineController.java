package fr.fredos.dvdtheque.allocine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.service.AllocineScrapingService;

@Controller
public class AllocineController {
	private AllocineScrapingService allocineScrapingService;
	
	@Autowired
    public AllocineController(AllocineScrapingService allocineScrapingService) {
        this.allocineScrapingService = allocineScrapingService;
    }
	@GetMapping("/byTitle")
	public ResponseEntity<FicheFilm> getAllocineFicheFilm(@PathVariable("title")String title) {
		return ResponseEntity.ok(allocineScrapingService.retrieveFicheFilmByTitle(title));
	}
}
