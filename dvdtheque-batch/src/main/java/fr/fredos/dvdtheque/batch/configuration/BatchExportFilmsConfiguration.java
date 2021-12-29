package fr.fredos.dvdtheque.batch.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.batch.film.writer.ExcelStreamFilmWriter;
import fr.fredos.dvdtheque.batch.model.Film;

@Configuration
@EnableBatchProcessing
@Lazy(true)
public class BatchExportFilmsConfiguration {
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfiguration.class);
	@Autowired
	protected JobBuilderFactory 					jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory 					stepBuilderFactory;
    @Autowired
    RestTemplate									restTemplate;
    @Autowired
    private Environment 							environment;
    public static String 							DVDTHEQUE_SERVICE_URL="dvdtheque-service.url";
	public static String 							DVDTHEQUE_SERVICE_ALL="dvdtheque-service.films";
	
    @Bean
    @Qualifier("exportFilmsJob")
    @Lazy(value = true)
	public Job exportFilmsJob() throws IOException {
    	logger.info("########### exportFilmsJob");
    	return jobBuilderFactory.get("exportFilms")
				.incrementer(new RunIdIncrementer())
				.start(exportFilmsStep())
				.build();
	}
    
    @Bean
    @Lazy(true)
    protected ListItemReader<Film> dvdthequeServiceFilmReader() {
    	logger.info("########### ListItemReader");
    	/*
    	ResponseEntity<List<Film>> filmList = restTemplate.exchange(environment.getRequiredProperty(DVDTHEQUE_SERVICE_URL)+environment.getRequiredProperty(DVDTHEQUE_SERVICE_ALL), 
    			HttpMethod.GET, 
    			null, 
    			new ParameterizedTypeReference<List<Film>>(){});
    	
    	return new ListItemReader<>(filmList.getBody());*/
    	
    	ResponseEntity<List<Film>> filmList2 = ResponseEntity.ok(new ArrayList<>());
    	return new ListItemReader<>(filmList2.getBody());
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Lazy(true)
    protected ExcelStreamFilmWriter excelFilmWriter() {
    	logger.info("########### excelFilmWriter");
    	return new ExcelStreamFilmWriter();
    }
    
    @Bean
    @Lazy(true)
    protected Step exportFilmsStep() {
    	logger.info("########### exportFilmsStep");
        return stepBuilderFactory.get("exportFilms")
                .<Film, Film>chunk(800).reader(dvdthequeServiceFilmReader())
                .writer(excelFilmWriter())
                .build();
    }
    @Bean
    @Lazy(true)
    public ObjectMapper mapper() {
    	return new ObjectMapper();
    }
}
