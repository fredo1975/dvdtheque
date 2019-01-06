package fr.fredos.dvdtheque.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.RestTemplate;
import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.batch.film.processor.FilmProcessor;
import fr.fredos.dvdtheque.batch.film.writer.FilmWriter;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;
import fr.fredos.dvdtheque.service.dto.FilmDto;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration{
	private static final String LISTE_DVD_FILE_NAME="csv.dvd.file.name";
	@Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("rippedFlagTasklet")
    public Tasklet rippedFlagTasklet;
    @Autowired
    @Qualifier("theMovieDbTasklet")
    public Tasklet theMovieDbTasklet;
    @Autowired
    Environment environment;
    
    @Bean
	protected Tasklet cleanDBTasklet() {
		return new Tasklet() {
			@Autowired
			protected PersonneService personneService;
			@Autowired
			protected FilmService filmService;
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				personneService.cleanAllPersonnes();
				filmService.cleanAllFilms();
				return RepeatStatus.FINISHED;
			}
		};
	}
    
    @Bean
    public FilmProcessor processor() {
        return new FilmProcessor();
    }
    @Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	@Bean
	public Job importFilmJob() {
		return jobBuilderFactory.get("importFilm").incrementer(new RunIdIncrementer()).start(cleanDB())
				.next(checkFilmStep()).next(setRippedFlag()).next(theMovieDb()).build();
	}
	@Bean
    protected Step theMovieDb() {
    	return stepBuilderFactory.get("theMovieDb").tasklet(theMovieDbTasklet).build();
    }
    @Bean
    protected Step cleanDB() {
    	return stepBuilderFactory.get("cleanDB").tasklet(cleanDBTasklet()).build();
    }
    @Bean
    protected Step setRippedFlag() {
    	return stepBuilderFactory.get("setRippedFlag").tasklet(rippedFlagTasklet).build();
    }
    
    @Bean
    public FlatFileItemReader<FilmCsvImportFormat> reader() {
    	FlatFileItemReader<FilmCsvImportFormat> csvFileReader = new FlatFileItemReader<>();
    	csvFileReader.setResource(new FileSystemResource(environment.getRequiredProperty(LISTE_DVD_FILE_NAME)));
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
