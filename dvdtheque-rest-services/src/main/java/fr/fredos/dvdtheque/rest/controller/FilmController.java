package fr.fredos.dvdtheque.rest.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.common.enums.FilmDisplayType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.exceptions.DvdthequeServerRestException;
import fr.fredos.dvdtheque.common.model.FilmDisplayTypeParam;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.rest.file.util.MultipartFileUtil;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;
import fr.fredos.dvdtheque.service.model.FilmListParam;
import fr.fredos.dvdtheque.tmdb.service.TmdbServiceClient;

@RestController
@ComponentScan({"fr.fredos.dvdtheque.rest"})
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
	@Autowired
	private JobLauncher jobLauncher;
    @Autowired
    private Job importFilmsJob;
    @Autowired
    private MultipartFileUtil multipartFileUtil;
    @Value("${eureka.instance.instance-id}")
    private String instanceId;
    @Value("${limit.film.size}")
    private int limitFilmSize;

	@GetMapping("/films/byPersonne")
	Personne findPersonne(@RequestParam(name="nom",required = false) String nom) {
		return personneService.findPersonneByName(nom);
	}
	@GetMapping("/films")
	ResponseEntity<List<Film>> findAllFilms(@RequestParam(name="displayType",required = false) String displayType) {
		try {
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,0,FilmOrigine.TOUS);
			return ResponseEntity.ok(filmService.findAllFilms(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@GetMapping("/films/genres")
	List<Genre> findAllGenres() {
		return filmService.findAllGenres();
	}
	@PutMapping("/films/cleanAllfilms")
	void cleanAllFilms() {
		filmService.cleanAllFilms();
	}
	@GetMapping("/films/byTitre/{titre}")
	Film findFilmByTitre(@PathVariable String titre) {
		return filmService.findFilmByTitre(titre);
	}
	@GetMapping("/films/byOrigine/{origine}")
	ResponseEntity<List<Film>> findAllFilmsByOrigine(@PathVariable String origine, @RequestParam(name="displayType",required = false) String displayType) {
		logger.debug("findAllFilmsByOrigine - instanceId="+instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,this.limitFilmSize,filmOrigine);
			return ResponseEntity.ok(filmService.findAllFilmsByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@GetMapping("/filmListParam/byOrigine/{origine}")
	ResponseEntity<FilmListParam> findFilmListParamByFilmDisplayTypeParam(@PathVariable String origine, @RequestParam(name="displayType",required = false) String displayType) {
		logger.debug("findFilmListParamByFilmDisplayType - instanceId="+instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,this.limitFilmSize,filmOrigine);
			return ResponseEntity.ok(filmService.findFilmListParamByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@GetMapping("/films/tmdb/byTitre/{titre}")
	Set<Film> findTmdbFilmByTitre(@PathVariable String titre) throws ParseException {
		return tmdbServiceClient.retrieveTmdbFilmListToDvdthequeFilmList(titre);
	}
	@GetMapping("/films/byId/{id}")
	Film findFilmById(@PathVariable Long id) {
		return filmService.findFilm(id);
	}
	@GetMapping("/films/byTmdbId/{tmdbid}")
	Boolean checkIfTmdbFilmExists(@PathVariable Long tmdbid) {
		return filmService.checkIfTmdbFilmExists(tmdbid);
	}
	@GetMapping("/realisateurs")
	List<Personne> findAllRealisateurs() {
		return filmService.findAllRealisateurs(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.TOUS));
	}
	@GetMapping("/realisateurs/byOrigine/{origine}")
	ResponseEntity<List<Personne>> findAllRealisateursByOrigine(@PathVariable String origine, @RequestParam(name="displayType",required = false) String displayType) {
		logger.info("findAllRealisateursByOrigine - instanceId="+instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,this.limitFilmSize,filmOrigine);
			if(FilmOrigine.TOUS.equals(filmOrigine)) {
				return ResponseEntity.ok(filmService.findAllRealisateurs(filmDisplayTypeParam));
			}
			return ResponseEntity.ok(filmService.findAllRealisateursByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@GetMapping("/acteurs")
	List<Personne> findAllActeurs() {
		return filmService.findAllActeurs(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.TOUS));
	}
	@GetMapping("/acteurs/byOrigine/{origine}")
	ResponseEntity<List<Personne>> findAllActeursByOrigine(@PathVariable String origine, @RequestParam(name="displayType",required = false) String displayType) {
		logger.info("findAllActeursByOrigine - instanceId="+instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,this.limitFilmSize,filmOrigine);
			if(FilmOrigine.TOUS.equals(filmOrigine)) {
				return ResponseEntity.ok(filmService.findAllActeurs(filmDisplayTypeParam));
			}
			return ResponseEntity.ok(filmService.findAllActeursByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@GetMapping("/personnes")
	List<Personne> findAllPersonne() {
		return personneService.findAllPersonne();
	}
	@PutMapping("/films/tmdb/{tmdbId}")
	ResponseEntity<Film> replaceFilm(@RequestBody Film film,@PathVariable Long tmdbId) throws Exception {
		try {
			Film filmOptional = filmService.findFilm(film.getId());
	
			if(filmOptional==null) {
				return ResponseEntity.notFound().build();
			}
			Film replacedFilm = tmdbServiceClient.replaceFilm(tmdbId, filmOptional);
			if(replacedFilm == null) {
				return ResponseEntity.badRequest().build();
			}
			return ResponseEntity.ok(replacedFilm);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@PutMapping("/films/update/{id}")
	ResponseEntity<Film> updateFilm(@RequestBody Film film,@PathVariable Long id) {
		try {
			Film filmOptional = filmService.findFilm(id);
			if(filmOptional==null) {
				return ResponseEntity.notFound().build();
			}
			// handle date rip
			if(filmOptional.getDvd() != null && !filmOptional.getDvd().isRipped() && film.getDvd().isRipped()) {
				film.getDvd().setDateRip(new Date());
			}
			Film mergedFilm = filmService.updateFilm(film);
			return ResponseEntity.ok(mergedFilm);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@PutMapping("/films/remove/{id}")
	ResponseEntity<Film> removeFilm(@PathVariable Long id) {
		try {
			Film filmOptional = filmService.findFilm(id);
			if(filmOptional==null) {
				return ResponseEntity.notFound().build();
			}
			filmService.removeFilm(filmOptional);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
	@PutMapping("/films/save/{tmdbId}")
	ResponseEntity<Film> saveFilm(@PathVariable Long tmdbId, @RequestBody String origine) throws Exception {
		Film savedFilm;
		logger.info("saveFilm - instanceId="+instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			savedFilm = tmdbServiceClient.saveTmbdFilm(tmdbId, filmOrigine);
			if(savedFilm==null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(savedFilm);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}
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
	@PostMapping("/films/import")
	ResponseEntity<Void> importFilmList(@RequestParam("file") MultipartFile file) {
		File resFile = null;
		try {
			resFile = this.multipartFileUtil.createFileToImport(file);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		try {
			JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
	    	jobParametersBuilder.addString("INPUT_FILE_PATH", resFile.getAbsolutePath());
	    	jobParametersBuilder.addLong("TIMESTAMP",new Date().getTime());
	    	JobExecution jobExecution  = jobLauncher.run(importFilmsJob, jobParametersBuilder.toJobParameters());
	    	long timeRunning = jobExecution.getCreateTime().getTime()-jobExecution.getEndTime().getTime();
	    	logger.debug("timeRunning="+timeRunning/100 + " s");
	    	logger.debug("getStatus="+jobExecution.getStatus()+ " getExitStatus="+jobExecution.getExitStatus());
		} catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | JobRestartException e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.noContent().build();
	}
	@PostMapping("/films/export")
	ResponseEntity<byte[]> exportFilmList(@RequestBody String origine) throws DvdthequeServerRestException, IOException{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLanguage(Locale.FRANCE);
    	headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    LocalDateTime localDate = LocalDateTime.now();
	    String fileName = "ListeDVDExport" + "-" + localDate.getSecond() + "-" + origine + ".xlsx";
	    try {
	    	List<Film> list = null;
	    	FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
	    	if(FilmOrigine.TOUS.equals(filmOrigine)) {
	    		FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS, 0, FilmOrigine.TOUS);
	    		list = filmService.findAllFilms(filmDisplayTypeParam);
	    	}else {
	    		list = filmService.findAllFilmsByCriteria(new FilmFilterCriteriaDto(null,null,null,null,null, null, filmOrigine));
	    	}
	    	if(list == null) {
	    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    	}
		    byte[] excelContent = this.excelFilmHandler.createByteContentFromFilmList(list);
	        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
	        headers.setContentLength(excelContent.length);
	        return new ResponseEntity<byte[]>(excelContent, headers, HttpStatus.OK);
	    }catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
