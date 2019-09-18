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
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;

import fr.fredos.dvdtheque.batch.configuration.BatchImportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.film.tasklet.RippedFlagTasklet;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.jms.publisher.DvdthequeJmsPublisher;

@SpringBootTest(classes = { BatchImportFilmsConfiguration.class,
		DvdthequeJmsPublisher.class,
		RippedFlagTasklet.class,
		fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class})
@DirtiesContext
public class BatchImportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	@Autowired
	public Job importFilmsJob;
	@Autowired
    protected Environment environment;
	private static final String LISTE_DVD_FILE_NAME="csv.dvd.file.name.import";
	public static final String TITRE_FILM_2001 = "2001 : L'ODYSSÉE DE L'ESPACE";
	public static final String TITRE_FILM_2046 = "2046";
	public static final String TITRE_FILM_40_ans = "40 ANS : MODE D'EMPLOI";
	public static final String REAL_NOM = "STANLEY KUBRICK";
	public static final String REAL_NOM2 = "WONG KAR-WAI";
	public static final String REAL_NOM3 = "JUDD APATOW";
	public static final String ACT1_NOM = "WILLIAM SYLVESTER";
	public static final String ACT2_NOM = "LEONARD ROSSITER";
	public static final String ACT3_NOM = "ROBERT BEATTY";
	public static final String ACT4_NOM = "FRANK MILLER";
	
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
	public void contextLoads() {
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
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addDate("TIMESTAMP", c.getTime());
		jobParametersBuilder.addString("INPUT_FILE_PATH", environment.getRequiredProperty(LISTE_DVD_FILE_NAME));
		JobExecution jobExecution = jobLauncherTestUtils(importFilmsJob).launchJob(jobParametersBuilder.toJobParameters());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		List<Film> films = filmService.findAllFilms();
		assertTrue(films.size()==7);
		boolean odysseyExists = false;
		boolean taxiDriverExists = false;
		boolean eraserHeadExists = false;
		
		for(Film film : films) {
			if(TITRE_FILM_2001.equals(film.getTitre())) {
				odysseyExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertTrue(film.isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
			}
			if(TITRE_FILM_2046.equals(film.getTitre())) {
				taxiDriverExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM2.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertTrue(film.isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
			}
			if(TITRE_FILM_40_ans.equals(film.getTitre())) {
				eraserHeadExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM3.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertFalse(film.isRipped());
				assertTrue(DvdFormat.DVD.name().equals(film.getDvd().getFormat().name()));
			}
			assertFilmIsNotNull(film);
		}
		assertTrue(odysseyExists);
		assertTrue(taxiDriverExists);
		assertTrue(eraserHeadExists);
	}
}
