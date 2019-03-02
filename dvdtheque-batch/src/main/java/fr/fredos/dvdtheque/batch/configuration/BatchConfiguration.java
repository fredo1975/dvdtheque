package fr.fredos.dvdtheque.batch.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.batch.film.processor.FilmProcessor;
import fr.fredos.dvdtheque.batch.film.writer.FilmWriter;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;
import fr.fredos.dvdtheque.service.dto.FilmDto;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration{
	protected Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);
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
    String[] headerTab = new String[]{"realisateur", "titre", "zonedvd","annee","acteurs"};
    
    @Bean
	protected Tasklet cleanDBTasklet() {
		return new Tasklet() {
			@Autowired
			protected IPersonneService personneService;
			@Autowired
			protected IFilmService filmService;
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
	public Job importFilmsJob() {
		return jobBuilderFactory.get("importFilms").incrementer(new RunIdIncrementer()).start(cleanDB())
				.next(checkFilmStep()).next(setRippedFlag()).build();
	}
	@Bean
	public Job exportFilmsJob() {
		return jobBuilderFactory.get("exportFilms").incrementer(new RunIdIncrementer()).start(cleanDB())
				.next(checkFilmStep()).next(setRippedFlag()).build();
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
    /*
    @Bean
    public ItemWriter<FilmCsvImportFormat> databaseCsvItemWriter() {
        FlatFileItemWriter<FilmCsvImportFormat> csvFileWriter = new FlatFileItemWriter<>();
 
        String exportFileHeader = "NAME;EMAIL_ADDRESS;PACKAGE";
        StringHeaderWriter headerWriter = new StringHeaderWriter(exportFileHeader);
        csvFileWriter.setHeaderCallback(headerWriter);
 
        String exportFilePath = "/tmp/students.csv";
        csvFileWriter.setResource(new FileSystemResource(exportFilePath));
 
        LineAggregator<FilmCsvImportFormat> lineAggregator = createStudentLineAggregator();
        csvFileWriter.setLineAggregator(lineAggregator);
 
        return csvFileWriter;
    }
    private LineAggregator<FilmCsvImportFormat> createStudentLineAggregator() {
        DelimitedLineAggregator<FilmCsvImportFormat> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(";");
 
        FieldExtractor<FilmCsvImportFormat> fieldExtractor = createStudentFieldExtractor();
        lineAggregator.setFieldExtractor(fieldExtractor);
 
        return lineAggregator;
    }
    private FieldExtractor<FilmCsvImportFormat> createStudentFieldExtractor() {
        BeanWrapperFieldExtractor<FilmCsvImportFormat> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"name", "emailAddress", "purchasedPackage"});
        return extractor;
    }*/
    
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
        DefaultLineMapper<FilmCsvImportFormat> filmLineMapper = new DefaultLineMapper<>();
        LineTokenizer filmCsvImportFormatLineTokenizer = createFilmCsvImportFormatLineTokenizer();
        filmLineMapper.setLineTokenizer(filmCsvImportFormatLineTokenizer);
        FieldSetMapper<FilmCsvImportFormat> filmCsvImportFormatInformationMapper = createFilmCsvImportFormatInformationMapper();
        filmLineMapper.setFieldSetMapper(filmCsvImportFormatInformationMapper);
        return filmLineMapper;
    }

    private LineTokenizer createFilmCsvImportFormatLineTokenizer() {
        DelimitedLineTokenizer filmCsvImportFormatLineTokenizer = new DelimitedLineTokenizer();
        filmCsvImportFormatLineTokenizer.setDelimiter(";");
        filmCsvImportFormatLineTokenizer.setNames(headerTab);
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
