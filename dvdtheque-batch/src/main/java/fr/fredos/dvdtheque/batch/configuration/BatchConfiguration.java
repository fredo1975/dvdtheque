package fr.fredos.dvdtheque.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.batch.film.processor.FilmProcessor;
import fr.fredos.dvdtheque.batch.film.writer.FilmWriter;
import fr.fredos.dvdtheque.dao.Application;
import fr.fredos.dvdtheque.service.dto.FilmDto;

@Configuration
@EnableBatchProcessing
@Import(Application.class)
public class BatchConfiguration{
	private static final String LISTE_DVD_FILE_NAME="csv.dvd.file.name";
	@Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public Tasklet cleanDBTasklet;
    @Autowired
    public Tasklet rippedFlagTasklet;
    @Autowired
    Environment environment;
    @Bean
    public FilmProcessor processor() {
        return new FilmProcessor();
    }
    
    @Bean
    public Job importFilmJob() {
            return jobBuilderFactory.get("importFilm")
            		.incrementer(new RunIdIncrementer())
            		.flow(cleanDB())
            		.next(checkFilmStep())
            		.next(setRippedFlag())
            		.end()
            		.build();
    }
    
    @Bean
    protected Step cleanDB() {
    	return stepBuilderFactory.get("cleanDB").tasklet(cleanDBTasklet).build();
    }
    @Bean
    protected Step setRippedFlag() {
    	return stepBuilderFactory.get("setRippedFlag").tasklet(rippedFlagTasklet).build();
    }
    @Bean
    JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
    @Bean
    public FlatFileItemReader<FilmCsvImportFormat> reader() {
    	FlatFileItemReader<FilmCsvImportFormat> csvFileReader = new FlatFileItemReader<>();
    	csvFileReader.setResource(new ClassPathResource(environment.getRequiredProperty(LISTE_DVD_FILE_NAME)));
        csvFileReader.setLinesToSkip(1);
        LineMapper<FilmCsvImportFormat> filmCsvImportFormatLineMapper = createFilmCsvImportFormatLineMapper();
        csvFileReader.setLineMapper(filmCsvImportFormatLineMapper);
        return csvFileReader;
    }
    
    private LineMapper<FilmCsvImportFormat> createFilmCsvImportFormatLineMapper() {
        DefaultLineMapper<FilmCsvImportFormat> studentLineMapper = new DefaultLineMapper<>();
        LineTokenizer filmCsvImportFormatLineTokenizer = createFilmCsvImportFormatLineTokenizer();
        studentLineMapper.setLineTokenizer(filmCsvImportFormatLineTokenizer);
        FieldSetMapper<FilmCsvImportFormat> filmCsvImportFormatInformationMapper = createFilmCsvImportFormatInformationMapper();
        studentLineMapper.setFieldSetMapper(filmCsvImportFormatInformationMapper);
        return studentLineMapper;
    }

    private LineTokenizer createFilmCsvImportFormatLineTokenizer() {
        DelimitedLineTokenizer filmCsvImportFormatLineTokenizer = new DelimitedLineTokenizer();
        filmCsvImportFormatLineTokenizer.setDelimiter(";");
        filmCsvImportFormatLineTokenizer.setNames(new String[]{"realisateur", "titre", "zonedvd","annee","acteurs"});
        return filmCsvImportFormatLineTokenizer;
    }

    private FieldSetMapper<FilmCsvImportFormat> createFilmCsvImportFormatInformationMapper() {
        BeanWrapperFieldSetMapper<FilmCsvImportFormat> filmCsvImportFormatInformationMapper = new BeanWrapperFieldSetMapper<>();
        filmCsvImportFormatInformationMapper.setTargetType(FilmCsvImportFormat.class);
        return filmCsvImportFormatInformationMapper;
    }
    @Bean
    protected FilmProcessor filmProcessor() {
        return new FilmProcessor();
    }
    @Bean
    protected FilmWriter filmWriter() {
        return new FilmWriter();
    }
    @Bean
    protected Step checkFilmStep() {
        return stepBuilderFactory.get("checkFilm")
                .<FilmCsvImportFormat, FilmDto>chunk(500)
                .reader(reader())
                .processor(filmProcessor())
                .writer(filmWriter())
                .build();
    }
}
