package fr.fredos.dvdtheque.rest.controller;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
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
    public static final String PUBLIC_PATH = "/public/";
    public static final String FILMS_PATH = "films/";
    public static final String SECURED_PATH = "/secured/";
    public static final String PUBLIC_BY_PERSONNE_PATH = PUBLIC_PATH+FILMS_PATH + "byPersonne";
    public static final String BY_TITRE_PATH = "byTitre/";
    public static final String BY_ORIGINE_PATH = "byOrigine/";
    public static final String PUBLIC_ACTEURS_PATH = PUBLIC_PATH + "acteurs/";
    public static final String PUBLIC_REALISATEURS_PATH = PUBLIC_PATH + "realisateurs/";
    public static final String PERSONNE_PATH = "personnes/";
    public static final String BY_ID_PATH = "byId/";
    public static final String PUBLIC_FIND_BY_TMDBID_PATH = PUBLIC_PATH + FILMS_PATH + "byTmdbId/";
    public static final String PUBLIC_FIND_ALL_FILMS_BY_ORIGINE_PATH = PUBLIC_PATH + BY_ORIGINE_PATH;
    public static final String PUBLIC_FIND_ALL_FILMS_PATH = PUBLIC_PATH+FILMS_PATH;
    public static final String PUBLIC_FIND_ALL_REALISATEURS_BY_ORIGINE_PATH = PUBLIC_REALISATEURS_PATH + BY_ORIGINE_PATH;
    public static final String PUBLIC_FIND_ALL_ACTEURS_BY_ORIGINE_PATH = PUBLIC_ACTEURS_PATH + BY_ORIGINE_PATH;
    public static final String PUBLIC_FIND_FILM_BY_ID = PUBLIC_PATH+FILMS_PATH+BY_ID_PATH;
    public static final String PUBLIC_FILM_LIST_PARAM_BY_ORIGINE_PATH = PUBLIC_PATH + FILMS_PATH + "filmListParam/byOrigine/";
    public static final String PUBLIC_FIND_ALL_GENRES_PATH = PUBLIC_PATH + FILMS_PATH + "genres";
    public static final String PUBLIC_FIND_FILM_TMDB_BY_TITRE_PATH = PUBLIC_PATH + FILMS_PATH + "tmdb/byTitre/";
    public static final String PUBLIC_PERSONNES_PATH = PUBLIC_PATH + "personnes";
    public static final String SECURED_UPDATE_FILM_BY_ID_PATH = SECURED_PATH+FILMS_PATH + "update/";
    public static final String SECURED_UPDATE_PERSONNE_BY_ID_PATH = SECURED_PATH+PERSONNE_PATH + "update/" + BY_ID_PATH;
    public static final String SECURED_REPLACE_FILM_BY_TMDBID_PATH = SECURED_PATH + FILMS_PATH + "replace/";
    public static final String SECURED_REMOVE_FILM_BY_ID_PATH = SECURED_PATH + FILMS_PATH + "remove/";
    public static final String SECURED_SAVE_FILM_BY_ID_PATH = SECURED_PATH+FILMS_PATH + "save/";
    public static final String SECURED_CLEAN_CACHES_PATH = SECURED_PATH + FILMS_PATH + "cleanCaches";
    public static final String SECURED_RETRIEVE_IMAGE_BY_ID_PATH = SECURED_PATH+FILMS_PATH + "retrieveImage/";
    public static final String RETRIEVE_ALL_IMAGES_PATH = SECURED_PATH + FILMS_PATH + "retrieveAllImages/";
    public static final String IMPORT_PATH = SECURED_PATH + FILMS_PATH + "import";
    public static final String EXPORT_PATH = SECURED_PATH + FILMS_PATH + "export";
    public static final String SECURED_CLEAN_ALL_FILMS_PATH = SECURED_PATH + "cleanAllfilms";
    
    // PUBLIC PATHS
	@GetMapping(PUBLIC_BY_PERSONNE_PATH)
	ResponseEntity<Personne> findPersonne(@RequestParam(name="nom",required = false) String nom) {
		try {
			return ResponseEntity.ok(personneService.findPersonneByName(nom));
		}catch (Exception e) {
			logger.error(format("an error occured while findPersonne nom='%s' ", nom),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FIND_ALL_FILMS_PATH)
	ResponseEntity<List<Film>> findAllFilms(@RequestParam(name="displayType",required = false) String displayType) {
		try {
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,0,FilmOrigine.TOUS);
			return ResponseEntity.ok(filmService.findAllFilms(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(format("an error occured while findAllFilms displayType='%s' ",displayType),e);
		}
		return ResponseEntity.badRequest().build();
	}
	@GetMapping(PUBLIC_FIND_ALL_GENRES_PATH)
	ResponseEntity<List<Genre>> findAllGenres() {
		try {
			return ResponseEntity.ok(filmService.findAllGenres());
		}catch (Exception e) {
			logger.error(format("an error occured while findAllGenres"),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping(PUBLIC_PATH+FILMS_PATH+BY_TITRE_PATH+"{titre}")
	ResponseEntity<Film> findFilmByTitre(@PathVariable String titre) {
		try {
			return ResponseEntity.ok(filmService.findFilmByTitre(titre));
		}catch (Exception e) {
			logger.error(format("an error occured while findFilmByTitre titre='%s' ", titre),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FIND_ALL_FILMS_BY_ORIGINE_PATH+"{origine}")
	ResponseEntity<List<Film>> findAllFilmsByOrigine(@PathVariable String origine, @RequestParam(name="displayType",required = false) String displayType) {
		logger.debug("findAllFilmsByOrigine - instanceId="+instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,this.limitFilmSize,filmOrigine);
			return ResponseEntity.ok(filmService.findAllFilmsByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(format("an error occured while findAllFilmsByOrigine origine='%s' and displayType='%s' ", origine,displayType),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FILM_LIST_PARAM_BY_ORIGINE_PATH+"{origine}")
	ResponseEntity<FilmListParam> findFilmListParamByFilmDisplayTypeParam(@PathVariable String origine, @RequestParam(name="displayType",required = false) String displayType) {
		logger.debug("findFilmListParamByFilmDisplayType - instanceId="+instanceId);
		try {
			FilmOrigine filmOrigine = FilmOrigine.valueOf(origine);
			FilmDisplayType filmDisplayType = FilmDisplayType.valueOf(displayType);
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(filmDisplayType,this.limitFilmSize,filmOrigine);
			return ResponseEntity.ok(filmService.findFilmListParamByFilmDisplayType(filmDisplayTypeParam));
		} catch (Exception e) {
			logger.error(format("an error occured while findFilmListParamByFilmDisplayTypeParam origine='%s' and displayType='%s' ", origine,displayType),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FIND_FILM_TMDB_BY_TITRE_PATH + "{titre}")
	ResponseEntity<List<Film>> findTmdbFilmByTitre(@PathVariable String titre) throws ParseException {
		try {
			return ResponseEntity.ok(tmdbServiceClient.retrieveTmdbFilmListToDvdthequeFilmList(titre));
		}catch(Exception e) {
			logger.error(format("an error occured while findTmdbFilmByTitre titre='%s' ", titre),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FIND_FILM_BY_ID + "{id}")
	ResponseEntity<Film> findFilmById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(filmService.findFilm(id));
		}catch(Exception e) {
			logger.error(format("an error occured while findFilmById id='%s' ", id),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FIND_BY_TMDBID_PATH+"{tmdbid}")
	ResponseEntity<Boolean> checkIfTmdbFilmExists(@PathVariable Long tmdbid) {
		try {
			return ResponseEntity.ok(filmService.checkIfTmdbFilmExists(tmdbid));
		}catch(Exception e) {
			logger.error(format("an error occured while checkIfTmdbFilmExists tmdbid='%s' ", tmdbid),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_REALISATEURS_PATH)
	ResponseEntity<List<Personne>> findAllRealisateurs() {
		try {
			return ResponseEntity.ok(filmService.findAllRealisateurs(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.TOUS)));
		}catch(Exception e) {
			logger.error(format("an error occured while findAllRealisateurs"),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FIND_ALL_REALISATEURS_BY_ORIGINE_PATH + "{origine}")
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
			logger.error(format("an error occured while findAllRealisateursByOrigine origine='%s' and displayType='%s' ", origine,displayType),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_ACTEURS_PATH)
	ResponseEntity<List<Personne>> findAllActeurs() {
		try {
			return ResponseEntity.ok(filmService.findAllActeurs(new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.TOUS)));
		}catch(Exception e) {
			logger.error(format("an error occured while findAllActeurs"),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_FIND_ALL_ACTEURS_BY_ORIGINE_PATH+"{origine}")
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
			logger.error(format("an error occured while findAllActeursByOrigine origine='%s' and displayType='%s' ", origine,displayType),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping(PUBLIC_PERSONNES_PATH)
	ResponseEntity<List<Personne>> findAllPersonne() {
		try {
			return ResponseEntity.ok(personneService.findAllPersonne());
		}catch (Exception e) {
			logger.error(format("an error occured while findAllPersonne"),e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	// SECURED PATHS
	@PutMapping(SECURED_REPLACE_FILM_BY_TMDBID_PATH+"{tmdbId}")
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
			logger.error("an error occured while replacing film tmdbId="+tmdbId,e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(SECURED_UPDATE_FILM_BY_ID_PATH+"{id}")
	ResponseEntity<Film> updateFilm(@RequestBody Film film,@PathVariable Long id) {
		try {
			Film filmOptional = filmService.findFilm(id);
			if(filmOptional==null) {
				return ResponseEntity.notFound().build();
			}
			// handle date rip
			if(filmOptional.getDvd() != null && !filmOptional.getDvd().isRipped() && film.getDvd() != null && film.getDvd().isRipped()) {
				film.getDvd().setDateRip(new Date());
			}
			Film mergedFilm = filmService.updateFilm(film);
			return ResponseEntity.ok(mergedFilm);
		} catch (Exception e) {
			logger.error("an error occured while updating film id="+id,e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(SECURED_REMOVE_FILM_BY_ID_PATH+"{id}")
	ResponseEntity<Film> removeFilm(@PathVariable Long id) {
		try {
			Film filmOptional = filmService.findFilm(id);
			if(filmOptional==null) {
				return ResponseEntity.notFound().build();
			}
			filmService.removeFilm(filmOptional);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("an error occured while removing film id="+id,e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(SECURED_CLEAN_CACHES_PATH)
	ResponseEntity<Void> cleanCaches() {
		try {
			filmService.cleanAllCaches();
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("an error occured while cleaning all caches",e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(SECURED_RETRIEVE_IMAGE_BY_ID_PATH+"{id}")
	ResponseEntity<Film> retrieveFilmImage(@PathVariable Long id) {
		try {
			Film filmOptional = filmService.findFilm(id);
			if(filmOptional==null) {
				return ResponseEntity.notFound().build();
			}
			tmdbServiceClient.retrieveFilmImage(filmOptional);
			Film mergedFilm = filmService.updateFilm(filmOptional);
			return ResponseEntity.ok(mergedFilm);
		} catch (Exception e) {
			logger.error("an error occured while retrieving image for film id="+id,e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(RETRIEVE_ALL_IMAGES_PATH)
	ResponseEntity<Void> retrieveAllFilmImages() {
		try {
			FilmDisplayTypeParam filmDisplayTypeParam = new FilmDisplayTypeParam(FilmDisplayType.TOUS,0,FilmOrigine.TOUS);
			List<Film> films = filmService.findAllFilms(filmDisplayTypeParam);
			for(Film film : films) {
				tmdbServiceClient.retrieveFilmImagesWhenNotExist(film);
				filmService.updateFilm(film);
			}
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("an error occured while retrieving all images",e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(SECURED_SAVE_FILM_BY_ID_PATH+"{tmdbId}")
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
			logger.error("an error occured while saving film tmdbId="+tmdbId,e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(SECURED_UPDATE_PERSONNE_BY_ID_PATH+"{id}")
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
	@PostMapping(IMPORT_PATH)
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
	    	jobLauncher.run(importFilmsJob, jobParametersBuilder.toJobParameters());
		} catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | JobRestartException e) {
			logger.error("an error occured while importFilmList",e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.noContent().build();
	}
	@PostMapping(EXPORT_PATH)
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
	    	logger.error("an error occured while exporting film list",e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping(SECURED_CLEAN_ALL_FILMS_PATH)
	void cleanAllFilms() {
		filmService.cleanAllFilms();
	}
}
