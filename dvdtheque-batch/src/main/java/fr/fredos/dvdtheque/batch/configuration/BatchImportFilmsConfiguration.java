package fr.fredos.dvdtheque.batch.configuration;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
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
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.batch.film.processor.FilmProcessor;
import fr.fredos.dvdtheque.batch.film.writer.DbFilmWriter;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;
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
    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }*/

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
    @Bean
    public Topic topic(){
        return new ActiveMQTopic("dvdtheque-topic");
    }
    @Bean
	protected Tasklet cleanDBTasklet() {
    	return new Tasklet() {
			@Autowired
			protected IFilmService filmService;
			/*@Autowired
			protected MessagePublisher messagePublisher;*/
			@Autowired
		    private JmsTemplate jmsTemplate;
			@Autowired
		    private Topic topic;
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				//messagePublisher.sendMessage(new JmsStatusMessage(JmsStatus.CLEAN_DB_INIT, null));
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_INIT, null));
				filmService.cleanAllFilms();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_COMPLETED, null));
				//messagePublisher.sendMessage(new JmsStatusMessage(JmsStatus.CLEAN_DB_COMPLETED, null));
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
