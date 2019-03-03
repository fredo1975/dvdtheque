package fr.fredos.dvdtheque.batch;

import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.batch.configuration.BatchImportFilmsConfiguration;
import fr.fredos.dvdtheque.batch.film.tasklet.RippedFlagTasklet;
import fr.fredos.dvdtheque.service.IFilmService;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BatchImportFilmsConfiguration.class,
		RippedFlagTasklet.class,
		fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class})
@ActiveProfiles("local")
public abstract class AbstractBatchFilmsConfigurationTest {
	@Autowired
	protected JobLauncher jobLauncher;
	@Autowired
	protected JobRepository jobRepository;
	@Autowired
	protected IFilmService filmService;
	protected JobLauncherTestUtils jobLauncherTestUtils;
	
	@Bean
	protected JobLauncherTestUtils jobLauncherTestUtils(Job job) throws NoSuchJobException {
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
		jobLauncherTestUtils.setJobRepository(jobRepository);
		return jobLauncherTestUtils;
	}
	
	
}
