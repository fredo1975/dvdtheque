package fr.fredos.dvdtheque.batch.configuration;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.film.tasklet.RetrieveDateInsertionTasklet;
import fr.fredos.dvdtheque.batch.film.tasklet.RippedFlagTasklet;
import fr.fredos.dvdtheque.batch.model.Dvd;
import fr.fredos.dvdtheque.batch.model.DvdBuilder;
import fr.fredos.dvdtheque.common.enums.DvdFormat;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {BatchImportFilmsConfiguration.class,
		RippedFlagTasklet.class,
		RetrieveDateInsertionTasklet.class,
		JmsMessageSender.class,
		BatchTestConfiguration.class})
public class BatchImportFilmsConfigurationTest{
	//protected Logger logger = LoggerFactory.getLogger(BatchImportFilmsConfigurationTest.class);
	@Autowired
	@Qualifier(value = "importFilmsJob")
	private Job 			job;
	@Autowired
	private JobRepository 	jobRepository;
	@MockBean
	private RestTemplate 	restTemplate;
	
	@Value("${batch.export.cron}")
	String cron;
	@Value("${csv.dvd.file.name.import}")
    private String path;
    private String INPUT_FILE_PATH="INPUT_FILE_PATH";
	public static final String TITRE_FILM_2001 = "2001 : L'ODYSSÉE DE L'ESPACE";
	public static final String TITRE_AD_ASTRA = "AD ASTRA";
	public static final String TITRE_FILM_2046 = "2046";
	public static final String TITRE_FILM_40_ans = "40 ANS : MODE D'EMPLOI";
	//public static final String TITRE_FILM_40_ans = "THIS IS 40";
	public static final String REAL_NOM = "STANLEY KUBRICK";
	public static final String REAL_NOM2 = "WONG KAR-WAI";
	public static final String REAL_NOM3 = "JUDD APATOW";
	public static final String ACT1_NOM = "WILLIAM SYLVESTER";
	public static final String ACT2_NOM = "LEONARD ROSSITER";
	public static final String ACT3_NOM = "ROBERT BEATTY";
	public static final String ACT4_NOM = "FRANK MILLER";
	public static final String REAL_NOM_AD_ASTRA = "JAMES GRAY";
	public static final String ACT1_AD_ASTRA = "GREG BRYK";
	public static final String ACT2_AD_ASTRA = "LOREN DEAN";
	public static final String ACT3_AD_ASTRA = "KIMBERLY ELISE";
	public static final String ACT4_AD_ASTRA = "LISAGAY HAMILTON";
	
	@Test
	public void launchImportFilmsJob() throws Exception {
		//logger.info("******** path="+path);
		Dvd dvd = new Dvd();
		dvd.setFormat(DvdFormat.DVD);
		dvd.setDateSortie(Date.from(LocalDate.of(2013, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		dvd.setZone(2);
		dvd.setAnnee(2022);
		dvd.setRipped(false);
		
		ResponseEntity<Dvd> dvdRes = new ResponseEntity<Dvd>(dvd,HttpStatus.ACCEPTED);
        Mockito.when(restTemplate.exchange(Mockito.any(String.class), 
        		Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<DvdBuilder>> any(),
                Mockito.<Class<Dvd>> any()))
        .thenReturn(dvdRes);
        
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		builder.addString(INPUT_FILE_PATH, path);
		JobParameters jobParameters = builder.toJobParameters();
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.afterPropertiesSet();
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		
		/*
		List<Film> films = filmService.findAllFilms(null);
		assertTrue(films.size()==8);
		boolean isAdAstraExists = false;
		boolean is2046Exists = false;
		boolean is40ansExists = false;
		
		for(Film film : films) {
			if(TITRE_AD_ASTRA.equals(film.getTitre())) {
				isAdAstraExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM_AD_ASTRA.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertTrue(FilmOrigine.EN_SALLE.equals(film.getOrigine()));
				FilmBuilder.assertFilmIsNotNull(film,true,0,FilmOrigine.EN_SALLE, "2019/09/18", "2020/01/13");
			}
			if(TITRE_FILM_2046.equals(film.getTitre())) {
				is2046Exists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM2.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertFalse(film.getDvd().isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
				FilmBuilder.assertFilmIsNotNull(film,true,0,FilmOrigine.DVD, "2004/05/20", "2018/06/18");
			}
			if(TITRE_FILM_40_ans.equals(film.getTitre())) {
				is40ansExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM3.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertTrue(film.getDvd().isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",Locale.FRANCE);
				Date ripDate = DateUtils.clearDate(sdf.parse("2019/07/24"));
				long temp = ChronoUnit.DAYS.between(DateUtils.clearDate(new Date()).toInstant(),DateUtils.clearDate(ripDate).toInstant());
				FilmBuilder.assertFilmIsNotNull(film,false,Long.valueOf(temp).intValue(),FilmOrigine.DVD, "2013/03/13", "2019/08/01");
			}
		}
		assertTrue(isAdAstraExists);
		assertTrue(is2046Exists);
		assertTrue(is40ansExists);*/
		
	}
}
