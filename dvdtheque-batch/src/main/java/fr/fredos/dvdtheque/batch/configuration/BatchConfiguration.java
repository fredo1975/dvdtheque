package fr.fredos.dvdtheque.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.fredos.dvdtheque.batch.film.processor.FilmProcessor;
import fr.fredos.dvdtheque.batch.film.tasklet.CleanDBTasklet;
import fr.fredos.dvdtheque.dao.Application;

@Configuration
@EnableBatchProcessing
@Import(Application.class)
public class BatchConfiguration{

	@Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public CleanDBTasklet cleanDBTasklet;
    
    @Bean
    public FilmProcessor processor() {
        return new FilmProcessor();
    }
    
    @Bean
    public Job importFilmJob() {
            return jobBuilderFactory.get("importFilm").incrementer(new RunIdIncrementer()).flow(cleanDB()).end().build();
    }
    
    @Bean
    protected Step cleanDB() {
    	return stepBuilderFactory.get("cleanDB").tasklet(cleanDBTasklet).build();
    }
    @Bean
    JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }

}
