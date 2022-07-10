package fr.fredos.dvdtheque.batch.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.model.Dvd;
import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.batch.model.Personne;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {BatchExportFilmsConfiguration.class,BatchTestConfiguration.class})
public class BatchExportFilmsConfigurationTest {
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfigurationTest.class);
	@Autowired
	@Qualifier(value = "runExportFilmsJob")
	private Job						job;
	
	@MockBean
	private RestTemplate 			restTemplate;
	
	@Autowired
	private JobRepository 			jobRepository;
	
	private Film buildfilm() {
		Film film = new Film();
		film.setAnnee(2012);
		film.setId(1l);
		film.setDvd(new Dvd());
		film.getDvd().setAnnee(2013);
		film.getDvd().setDateRip(Date.from(LocalDate.of(2013, 8, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		film.getDvd().setFormat(DvdFormat.DVD);
		film.getDvd().setRipped(true);
		film.getDvd().setZone(2);
		film.setDateInsertion(Date.from(LocalDate.of(2013, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		film.setActeur(new HashSet<Personne>());
		Personne act1 = new Personne();
		act1.setId(1l);
		act1.setNom("Tom Cruise");
		film.getActeur().add(act1);
		Personne real = new Personne();
		real.setId(2l);
		real.setNom("David Lynch");
		film.setRealisateur(new HashSet<Personne>());
		film.getRealisateur().add(real);
		film.setTitre("film");
		film.setOrigine(FilmOrigine.DVD);
		film.setTmdbId(1l);
		film.setVu(false);
		return film;
	}
	@SuppressWarnings("unchecked")
	@Test
	public void launchExportFilmsJob() throws Exception {
		List<Film> l = new ArrayList<>();
		l.add(buildfilm());
        ResponseEntity<List<Film>> filmList = new ResponseEntity<List<Film>>(l,HttpStatus.ACCEPTED);
        Mockito.when(restTemplate.exchange(Mockito.any(String.class),
        		Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<?>> any(),
    			Mockito.any(ParameterizedTypeReference.class)))
        .thenReturn(filmList);
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.afterPropertiesSet();
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
