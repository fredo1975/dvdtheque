package fr.fredos.dvdtheque.rest.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import fr.fredos.dvdtheque.common.exceptions.DvdthequeServerRestException;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@RestController
@ComponentScan({"fr.fredos.dvdtheque.service,fr.fredos.dvdtheque.dao.model.repository,fr.fredos.dvdtheque.batch"})
@RequestMapping("/dvdtheque")
public class FilmController {
	protected Logger logger = LoggerFactory.getLogger(FilmController.class);
	@Autowired
	private IFilmService filmService;
	@Autowired
	protected IPersonneService personneService;
	@Autowired
    private TmdbServiceClient tmdbServiceClient;
	@Autowired
    private ExcelFilmHandler excelFilmHandler;
	/*
	@Autowired
	private JobLauncher jobLauncher;
    @Autowired
    private Job exportFilmsJob;*/
	@CrossOrigin
	@GetMapping("/films/byPersonne")
	Personne findPersonne(@RequestParam(name="nom",required = false) String nom) {
		return personneService.findPersonneByName(nom);
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
	@GetMapping("/films/tmdb/byTitre/{titre}")
	Set<Film> findTmdbFilmByTitre(@PathVariable String titre) throws ParseException {
		return tmdbServiceClient.retrieveTmdbFilmListToDvdthequeFilmList(titre);
	}
	@CrossOrigin
	@GetMapping("/films/byId/{id}")
	Film findFilmById(@PathVariable Long id) {
		return filmService.findFilm(id);
	}
	@CrossOrigin
	@GetMapping("/films/byTmdbId/{tmdbid}")
	Boolean checkIfTmdbFilmExists(@PathVariable Long tmdbid) {
		return filmService.checkIfTmdbFilmExists(tmdbid);
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
	@PutMapping("/films/tmdb/{tmdbId}")
	ResponseEntity<Film> replaceFilm(@RequestBody Film film,@PathVariable Long tmdbId) throws Exception {
		Film filmOptional = filmService.findFilm(film.getId());

		if(filmOptional==null) {
			return ResponseEntity.notFound().build();
		}
		Film replacedFilm = tmdbServiceClient.replaceFilm(tmdbId, filmOptional);
		return ResponseEntity.ok(replacedFilm);
	}
	@CrossOrigin
	@PutMapping("/films/update/{id}")
	ResponseEntity<Object> updateFilm(@RequestBody Film film,@PathVariable Long id) {
		Film filmOptional = filmService.findFilm(id);
		if(filmOptional==null) {
			return ResponseEntity.notFound().build();
		}
		filmService.updateFilm(film);
		return ResponseEntity.noContent().build();
	}
	
	@CrossOrigin
	@PutMapping("/films/save/{tmdbId}")
	ResponseEntity<Film> saveFilm(@PathVariable Long tmdbId) throws Exception {
		Film savedFilm;
		try {
			savedFilm = tmdbServiceClient.saveTmbdFilm(tmdbId);
			if(savedFilm==null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(savedFilm);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@CrossOrigin
	@PutMapping("/personnes/byId/{id}")
	ResponseEntity<Object> updatePersonne(@RequestBody Personne p,@PathVariable Long id) {
		Personne personne = personneService.findByPersonneId(id);
		if(personne==null) {
			return ResponseEntity.notFound().build();
		}
		if(StringUtils.isNotEmpty(p.getNom())) {
			personne.setNom(StringUtils.upperCase(p.getNom()));
		}
		personneService.updatePersonne(personne);
		logger.info(personne.toString());
		return ResponseEntity.noContent().build();
	}
	
	@CrossOrigin
	@PostMapping("/films/export")
	ResponseEntity<byte[]> exportFilmList() throws DvdthequeServerRestException, IOException{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLanguage(Locale.FRANCE);
    	headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		SXSSFWorkbook workBook = null;
	    byte[] excelContent = null;
	    LocalDateTime localDate = LocalDateTime.now();
	    String fileName = "ListeDVDExport" + "-" + localDate.getSecond() + ".xlsx";
	    try{
	    	List<Film> list = filmService.findAllFilms();
	    	workBook = this.excelFilmHandler.getWorkBook();
	    	this.excelFilmHandler.createSheet(workBook);
	    	this.excelFilmHandler.setRow(null);
	    	for(Film film : list) {
	    		this.excelFilmHandler.writeBook(film);
	    	}
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	workBook.write(baos);
	    	excelContent = baos.toByteArray();
	    /*}catch (Exception ex) {
	    	ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage(),ex);
            return new ResponseEntity<byte[]>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        */}finally {
            if (null != workBook) {
                try {
                	workBook.close();
                } catch (IOException eio) {
                    logger.error("Error Occurred while exporting to XLS ", eio);
                }
            }
        }
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentLength(excelContent.length);
        return new ResponseEntity<byte[]>(excelContent, headers, HttpStatus.OK);
	}
}
