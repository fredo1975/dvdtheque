package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import fr.fredos.dvdtheque.batch.configuration.BatchImportFilmsConfiguration;

@ContextConfiguration(classes={BatchImportFilmsConfiguration.class,OAuth2ClientConfiguration.class})
@SpringBootTest(classes = {OAuth2ClientConfiguration.class,BatchImportFilmsConfiguration.class}, properties = "spring.batch.job.enabled=false" )
@Disabled
public class BatchImportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	@Autowired
	@Qualifier(value = "importFilmsJob")
	public Job job;
	
	private static final String LISTE_DVD_FILE_NAME="csv.dvd.file.name.import";
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
	@Autowired
	JobLauncherTestUtils 					jobLauncherTestUtils;
	
	@BeforeEach
	public void init() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJob(job);
	}
	/*
	@Bean
	protected JobLauncherTestUtils jobLauncherTestUtils() throws NoSuchJobException {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJob(importFilmsJob);
		return jobLauncherTestUtils;
	}*/
 
	@Test
	public void contextLoads() {
	}
	@Test
	public void launchCleanDBStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("cleanDBStep");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	public void launchImportFilmsJob() throws Exception {
		Calendar c = Calendar.getInstance();
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addDate("TIMESTAMP", c.getTime());
		jobParametersBuilder.addString("INPUT_FILE_PATH", environment.getRequiredProperty(LISTE_DVD_FILE_NAME));
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());
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
