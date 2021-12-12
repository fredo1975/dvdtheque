package fr.fredos.dvdtheque.batch.configuration;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.film.writer.ExcelStreamFilmWriter;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;

@Configuration
@EnableBatchProcessing
public class BatchExportFilmsConfiguration {
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfiguration.class);
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    @Autowired
    private RestTemplate restTemplate;
    @Bean
	public Job exportFilmsJob() throws IOException {
    	return jobBuilderFactory.get("exportFilms")
				.incrementer(new RunIdIncrementer())
				.start(exportFilmsStep())
				.build();
	}
    @Bean
    protected ListItemReader<Film> dbFilmReader() {
    	return new ListItemReader<>(filmService.findAllFilms(null));
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    protected ExcelStreamFilmWriter excelFilmWriter() {
    	return new ExcelStreamFilmWriter();
    }
    
    @Bean
    protected Step exportFilmsStep() {
        return stepBuilderFactory.get("exportFilms")
                .<Film, Film>chunk(800).reader(dbFilmReader())
                .writer(excelFilmWriter())
                .build();
    }
}
