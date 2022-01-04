package fr.fredos.dvdtheque.allocine.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.dto.FicheFilmDto;
import fr.fredos.dvdtheque.allocine.service.AllocineService;

@Controller
@RequestMapping("/dvdtheque-allocine-service")
public class AllocineController {
	private AllocineService allocineService;
	@Autowired
    private ModelMapper modelMapper;
	@Autowired
    public AllocineController(AllocineService allocineScrapingService) {
        this.allocineService = allocineScrapingService;
    }
	@GetMapping("/byTitle")
	public ResponseEntity<FicheFilmDto> getAllocineFicheFilmByTitle(@RequestParam(name = "title", required = false) String title) {
		return ResponseEntity.ok(convertToDto(allocineService.retrieveFicheFilmByTitle(title)));
	}
	private FicheFilmDto convertToDto(FicheFilm ficheFilm) {
		FicheFilmDto ficheFilmDto = modelMapper.map(ficheFilm, FicheFilmDto.class);
	    return ficheFilmDto;
	}
	/*
	@GetMapping("/byFicheFilmId/{ficheFilmId}")
	public ResponseEntity<FicheFilm> getAllocineFicheFilmByFicheFilmId(@PathVariable("ficheFilmId")Integer ficheFilmId) {
		return ResponseEntity.ok(allocineService.retrievefindByFicheFilmId(ficheFilmId));
	}*/
	@PostMapping("/scraping-fichefilm")
	public ResponseEntity<Void> launchAllocineScrapingFicheFilm() {
		allocineService.retrieveAllocineScrapingFicheFilm();
		return ResponseEntity.noContent().build();
	}
}
