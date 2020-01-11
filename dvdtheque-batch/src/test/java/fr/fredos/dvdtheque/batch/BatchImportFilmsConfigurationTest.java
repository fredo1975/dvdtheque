package fr.fredos.dvdtheque.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
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

import fr.fredos.dvdtheque.batch.configuration.BatchImportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.configuration.MessageConsumer;
import fr.fredos.dvdtheque.batch.film.tasklet.RippedFlagTasklet;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import fr.fredos.dvdtheque.common.utils.DateUtils;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.utils.FilmBuilder;

@SpringBootTest(classes = { BatchImportFilmsConfiguration.class,MessageConsumer.class,
		RippedFlagTasklet.class,
		fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class})
public class BatchImportFilmsConfigurationTest extends AbstractBatchFilmsConfigurationTest{
	@Autowired
	public Job importFilmsJob;
	@Autowired
    protected Environment environment;
	/*
	@Autowired
	protected MessageConsumer messageConsumer;*/
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
	
	@Before
	public void init() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJob(importFilmsJob);
	}
	@After 
	public void consumeMessages() {
		
	}
	@Test
	public void contextLoads() {
	}
	@Test
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
		List<Film> films = filmService.findAllFilms(null);
		assertTrue(films.size()==7);
		boolean is2001odysseyExists = false;
		boolean is2046Exists = false;
		boolean is40ansExists = false;
		
		for(Film film : films) {
			if(TITRE_FILM_2001.equals(film.getTitre())) {
				is2001odysseyExists = true;
				Personne real = film.getRealisateurs().iterator().next();
				assertTrue(REAL_NOM.equals(real.getNom()));
				Set<Personne> acteurs = film.getActeurs();
				assertTrue(CollectionUtils.isNotEmpty(acteurs));
				assertTrue(acteurs.size()>7);
				assertTrue(FilmOrigine.EN_SALLE.equals(film.getOrigine()));
				FilmBuilder.assertFilmIsNotNull(film,true,0,FilmOrigine.EN_SALLE, "1968/09/26", "2019/08/01");
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
				FilmBuilder.assertFilmIsNotNull(film,true,0,FilmOrigine.DVD, "2004/10/20", "2019/08/01");
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date ripDate = DateUtils.clearDate(sdf.parse("2019/07/24"));
				ChronoUnit.DAYS.between(ripDate.toInstant(),new Date().toInstant());
				long temp = ChronoUnit.DAYS.between(new Date().toInstant(),ripDate.toInstant());
				FilmBuilder.assertFilmIsNotNull(film,false,Long.valueOf(temp).intValue(),FilmOrigine.DVD, "2013/03/13", "2019/08/01");
			}
		}
		assertTrue(is2001odysseyExists);
		assertTrue(is2046Exists);
		assertTrue(is40ansExists);
	}
}
