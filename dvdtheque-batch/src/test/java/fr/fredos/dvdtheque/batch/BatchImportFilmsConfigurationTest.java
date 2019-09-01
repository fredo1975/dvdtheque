package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.fredos.dvdtheque.batch.configuration.BatchImportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.film.tasklet.RippedFlagTasklet;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

@SpringBootTest(classes = { BatchImportFilmsConfiguration.class,
		RippedFlagTasklet.class,
		fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class})
public class BatchImportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	
	@Autowired
	public Job importFilmsJob;
	public static final String TITRE_FILM_BLUE_VELVET = "BLUE VELVET";
	public static final String TITRE_FILM_TAXI_DRIVER = "TAXI DRIVER";
	public static final String TITRE_FILM_ERASERHEAD = "ERASERHEAD";
	public static final Integer ANNEE_BLUE_VELVET = 1986;
	public static final String REAL_NOM = "DAVID LYNCH";
	public static final String REAL_NOM2 = "MARTIN SCORSESE";
	public static final String ACT1_NOM = "Kyle MacLachlan";
	public static final String ACT2_NOM = "Laura Dern";
	public static final String ACT3_NOM = "Dennis Hopper";
	public static final String ACT4_NOM = "Isabella Rossellini";
	
	private void assertFilmIsNotNull(Film film) {
		assertNotNull(film);
		assertNotNull(film.getId());
		assertNotNull(film.getTitre());
		assertNotNull(film.getAnnee());
		assertNotNull(film.getDvd());
		assertNotNull(film.getOverview());
		assertTrue(CollectionUtils.isNotEmpty(film.getActeurs()));
		assertTrue(film.getActeurs().size()>7);
		assertTrue(CollectionUtils.isNotEmpty(film.getRealisateurs()));
		assertTrue(film.getRealisateurs().size()==1);
	}
	@Before
	public void init() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJob(importFilmsJob);
	}
	
	@Test
	//@Ignore
	public void launchCleanDBStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils(importFilmsJob).launchStep("cleanDBStep");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	public void launchImportFilmsJob() throws Exception {
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils(importFilmsJob).launchJob(jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		List<Film> films = filmService.findAllFilms();
		assertTrue(films.size()==11);
		boolean blueVelvetExists = false;
		boolean taxiDriverExists = false;
		boolean eraserHeadExists = false;
		
		for(Film film : films) {
			if(TITRE_FILM_BLUE_VELVET.equals(film.getTitre())) {
				blueVelvetExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertTrue(film.isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
			}
			if(TITRE_FILM_TAXI_DRIVER.equals(film.getTitre())) {
				taxiDriverExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM2.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertTrue(film.isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
			}
			if(TITRE_FILM_ERASERHEAD.equals(film.getTitre())) {
				eraserHeadExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertFalse(film.isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
			}
			assertFilmIsNotNull(film);
		}
		assertTrue(blueVelvetExists);
		assertTrue(taxiDriverExists);
		assertTrue(eraserHeadExists);
	}
}
