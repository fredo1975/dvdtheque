package fr.fredos.dvdtheque.allocine.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
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
    public AllocineController(AllocineService allocineService) {
        this.allocineService = allocineService;
    }
	@GetMapping("/byTitle")
	public ResponseEntity<List<FicheFilmDto>> getAllocineFicheFilmByTitle(@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "titleO", required = false) String titleO) {
		List<FicheFilm> l = allocineService.retrieveFicheFilmByTitle(title);
		List<FicheFilmDto> ll = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(l)) {
			for(FicheFilm ficheFilm : l) {
				ll.add(convertToDto(ficheFilm));
			}
		}else {
			l = allocineService.retrieveFicheFilmByTitle(titleO);
			if(CollectionUtils.isNotEmpty(l)) {
				for(FicheFilm ficheFilm : l) {
					ll.add(convertToDto(ficheFilm));
				}
			}
		}
		return ResponseEntity.ok(ll);
	}
	@GetMapping("/byId")
	public ResponseEntity<FicheFilmDto> getAllocineFicheFilmById(@RequestParam(name = "id", required = true) Integer id) {
		Optional<FicheFilm> ficheFilm = allocineService.retrieveFicheFilm(id.intValue());
		if(ficheFilm.isPresent()) {
			return ResponseEntity.ok(convertToDto(ficheFilm.get()));
		}
		return ResponseEntity.notFound().build();
	}
	private FicheFilmDto convertToDto(FicheFilm ficheFilm) {
		if(ficheFilm != null) {
			FicheFilmDto ficheFilmDto = modelMapper.map(ficheFilm, FicheFilmDto.class);
		    return ficheFilmDto;
		}
		return null;
	}
	/*
	@GetMapping("/byFicheFilmId/{ficheFilmId}")
	public ResponseEntity<FicheFilm> getAllocineFicheFilmByFicheFilmId(@PathVariable("ficheFilmId")Integer ficheFilmId) {
		return ResponseEntity.ok(allocineService.retrievefindByFicheFilmId(ficheFilmId));
	}*/
	@PostMapping("/scraping-fichefilm")
	public ResponseEntity<Void> launchAllocineScrapingFicheFilm() {
		allocineService.scrapAllAllocineFicheFilm();
		return ResponseEntity.noContent().build();
	}
}
