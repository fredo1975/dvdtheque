package fr.fredos.dvdtheque.batch.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.messaging.support.MessageBuilder;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.batch.film.processor.FilmProcessor;
import fr.fredos.dvdtheque.batch.film.writer.DbFilmWriter;
import fr.fredos.dvdtheque.batch.jms.model.JmsStatusMessage;
import fr.fredos.dvdtheque.batch.jms.publisher.MessagePublisher;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;

@Configuration
@EnableBatchProcessing
public class BatchImportFilmsConfiguration{
	protected Logger logger = LoggerFactory.getLogger(BatchImportFilmsConfiguration.class);
	String[] headerTab = new String[]{"realisateur", "titre", "zonedvd","annee","acteurs","ripped","ripdate","dvdformat","tmdbId"};
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("rippedFlagTasklet")
    protected Tasklet rippedFlagTasklet;
    
	    /*
    @Output(Source.OUTPUT)
	@Autowired
	private MessageChannel messageChannel;
    
    
    @EnableBinding(Sink.class)
	static class TestSink {
    	protected Logger logger = LoggerFactory.getLogger(TestSink.class);

		@StreamListener(Sink.INPUT1)
		public void receive(String data) {
			logger.info("Data received from customer-1..." + data);
		}

		@StreamListener(Sink.INPUT2)
		public void receiveX(String data) {
			logger.info("Data received from customer-2..." + data);
		}
	}
    interface Sink {

		String INPUT1 = "input1";
		String INPUT2 = "input2";


		@Input(INPUT1)
		SubscribableChannel input1();


		@Input(INPUT2)
		SubscribableChannel input2();

	}*/
    
    @Bean
	protected Tasklet cleanDBTasklet() {
    	return new Tasklet() {
			@Autowired
			protected IFilmService filmService;
			@Autowired
		    private MessagePublisher messagePublisher;
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				this.messagePublisher.sendMessage(new JmsStatusMessage(JmsStatus.CLEAN_DB_INIT, null));
				filmService.cleanAllFilms();
				this.messagePublisher.sendMessage(new JmsStatusMessage(JmsStatus.CLEAN_DB_COMPLETED, null));
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
		return jobBuilderFactory.get("importFilms").incrementer(new RunIdIncrementer()).start(cleanDBStep())
				.next(importFilmsStep()).next(setRippedFlagStep()).build();
	}
	
    @Bean
    protected Step cleanDBStep() {
    	return stepBuilderFactory.get("cleanDBStep").tasklet(cleanDBTasklet()).build();
    }
    @Bean
    protected Step setRippedFlagStep() {
    	return stepBuilderFactory.get("rippedFlagStep").tasklet(rippedFlagTasklet).build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<FilmCsvImportFormat> reader(@Value("#{jobParameters[INPUT_FILE_PATH]}") String inputFilePath) {
    	FlatFileItemReader<FilmCsvImportFormat> csvFileReader = new FlatFileItemReader<>();
    	csvFileReader.setResource(new FileSystemResource(inputFilePath));
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
    protected DbFilmWriter filmWriter() {
    	return new DbFilmWriter();
    }
    @Bean
    protected Step importFilmsStep() {
        return stepBuilderFactory.get("importFilmsStep")
                .<FilmCsvImportFormat, Film>chunk(500)
                .reader(reader(null))
                .processor(filmProcessor())
                .writer(filmWriter())
                .build();
    }
}
