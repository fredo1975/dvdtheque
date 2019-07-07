package fr.fredos.dvdtheque.batch;

import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.service.IFilmService;
@RunWith(SpringRunner.class)
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
