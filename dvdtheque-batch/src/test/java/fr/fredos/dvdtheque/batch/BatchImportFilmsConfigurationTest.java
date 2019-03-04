package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;
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

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;


public class BatchImportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	
	@Autowired
	public Job importFilmsJob;
	public static final String TITRE_FILM_BLUE_VELVET = "BLUE VELVET";
	public static final String TITRE_FILM_TAXI_DRIVER = "TAXI DRIVER";
	public static final Integer ANNEE_BLUE_VELVET = 1986;
	public static final String REAL_NOM = "DAVID LYNCH";
	public static final String REAL_NOM2 = "MARTIN SCORSESE";
	public static final String ACT1_NOM = "Kyle MacLachlan";
	public static final String ACT2_NOM = "Laura Dern";
	public static final String ACT3_NOM = "Dennis Hopper";
	public static final String ACT4_NOM = "Isabella Rossellini";
	@Before
	public void init() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJob(importFilmsJob);
	}
	
	@Test
	public void launchCleanDBStep() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils(importFilmsJob).launchStep("cleanDBStep");
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	public void launchJob() throws Exception {
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils(importFilmsJob).launchJob(jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		List<Film> films = filmService.findAllFilms();
		assertTrue(films.size()==9);
		boolean blueVelvetExists = false;
		boolean taxiDriverExists = false;
		for(Film film : films) {
			if(TITRE_FILM_BLUE_VELVET.equals(film.getTitre())) {
				blueVelvetExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()==7);
			}
			if(TITRE_FILM_TAXI_DRIVER.equals(film.getTitre())) {
				taxiDriverExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM2.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()==7);
			}
		}
		assertTrue(blueVelvetExists);
		assertTrue(taxiDriverExists);
	}
}
