package fr.fredos.dvdtheque.batch.configuration;

import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
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
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;

@Configuration
@EnableBatchProcessing
public class BatchImportFilmsConfiguration{
	protected Logger logger = LoggerFactory.getLogger(BatchImportFilmsConfiguration.class);
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("rippedFlagTasklet")
    protected Tasklet rippedFlagTasklet;
    @Autowired
    @Qualifier("retrieveDateInsertionTasklet")
    protected Tasklet retrieveDateInsertionTasklet;
    @Autowired
    private JmsTemplate jmsTemplate;
	@Autowired
    private Topic topic;
	@Value( "${import.chunk.size}" )
	private int chunkSize;
	class DvdthequeJobResultListener implements JobExecutionListener{
		@Override
		public void beforeJob(JobExecution jobExecution) {
			logger.debug("beforeJob");
			jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.IMPORT_INIT, null,0l,JmsStatus.IMPORT_INIT.statusValue()));
		}

		@Override
		public void afterJob(JobExecution jobExecution) {
			long executionTime = jobExecution.getEndTime().getTime()-jobExecution.getStartTime().getTime();
			logger.debug("afterJob executionTime="+executionTime/100 + " s");
			if( jobExecution.getStatus() == BatchStatus.COMPLETED ){
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.IMPORT_COMPLETED_SUCCESS, null,executionTime,JmsStatus.IMPORT_COMPLETED_SUCCESS.statusValue()));
			}else if(jobExecution.getStatus() == BatchStatus.FAILED){
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.IMPORT_COMPLETED_ERROR, null,executionTime,JmsStatus.IMPORT_COMPLETED_ERROR.statusValue()));
			}
		}
	}
    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
    @Bean
    public Topic topic(){
        return new ActiveMQTopic("topic");
    }
    @Bean
	protected Tasklet cleanDBTasklet() {
    	return new Tasklet() {
			@Autowired
			protected IFilmService filmService;
			@Autowired
		    private JmsTemplate jmsTemplate;
			@Autowired
		    private Topic topic;
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				StopWatch watch = new StopWatch();
				watch.start();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_INIT, null,0l,JmsStatus.CLEAN_DB_INIT.statusValue()));
				filmService.cleanAllFilms();
				watch.stop();
				jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_COMPLETED, null,watch.getTime(),JmsStatus.CLEAN_DB_COMPLETED.statusValue()));
				logger.debug("database cleaning Time Elapsed: " + watch.getTime());
				return RepeatStatus.FINISHED;
			}
		};
	}
    
    @Bean
    public FilmProcessor processor() {
        return new FilmProcessor();
    }
    
	@Bean
	public Job importFilmsJob() throws Exception {
		return jobBuilderFactory.get("importFilms").listener(new DvdthequeJobResultListener()).incrementer(new RunIdIncrementer()).start(cleanDBStep())
				.next(importFilmsStep()).next(setRippedFlagStep()).next(setRetrieveDateInsertionStep()).build();
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
    protected Step setRetrieveDateInsertionStep() {
    	return stepBuilderFactory.get("retrieveDateInsertionStep").tasklet(retrieveDateInsertionTasklet).build();
    }
    @Bean
    @StepScope
    public FlatFileItemReader<FilmCsvImportFormat> reader(@Value("#{jobParameters[INPUT_FILE_PATH]}") String inputFilePath) {
    	StopWatch watch = new StopWatch();
		watch.start();
    	jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILE_ITEM_READER_INIT, null,0l,JmsStatus.FILE_ITEM_READER_INIT.statusValue()));
    	FlatFileItemReader<FilmCsvImportFormat> csvFileReader = new FlatFileItemReader<>();
    	csvFileReader.setResource(new FileSystemResource(inputFilePath));
        csvFileReader.setLinesToSkip(1);
        LineMapper<FilmCsvImportFormat> filmCsvImportFormatLineMapper = createFilmCsvImportFormatLineMapper();
        csvFileReader.setLineMapper(filmCsvImportFormatLineMapper);
        watch.stop();
        jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILE_ITEM_READER_COMPLETED, null,watch.getTime(),JmsStatus.FILE_ITEM_READER_COMPLETED.statusValue()));
		logger.debug("reader Time Elapsed: " + watch.getTime());
        return csvFileReader;
    }
    
    private LineMapper<FilmCsvImportFormat> createFilmCsvImportFormatLineMapper() {
    	StopWatch watch = new StopWatch();
		watch.start();
    	jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_MAPPER_INIT, null,0l,JmsStatus.FILM_CSV_LINE_MAPPER_INIT.statusValue()));
        DefaultLineMapper<FilmCsvImportFormat> filmLineMapper = new DefaultLineMapper<>();
        LineTokenizer filmCsvImportFormatLineTokenizer = createFilmCsvImportFormatLineTokenizer();
        filmLineMapper.setLineTokenizer(filmCsvImportFormatLineTokenizer);
        FieldSetMapper<FilmCsvImportFormat> filmCsvImportFormatInformationMapper = createFilmCsvImportFormatInformationMapper();
        filmLineMapper.setFieldSetMapper(filmCsvImportFormatInformationMapper);
        watch.stop();
        jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_MAPPER_COMPLETED, null,watch.getTime(),JmsStatus.FILM_CSV_LINE_MAPPER_COMPLETED.statusValue()));
		logger.debug("createFilmCsvImportFormatLineMapper Time Elapsed: " + watch.getTime());
        return filmLineMapper;
    }

    private LineTokenizer createFilmCsvImportFormatLineTokenizer() {
    	StopWatch watch = new StopWatch();
		watch.start();
    	jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_TOKENIZER_INIT, null,0l,JmsStatus.FILM_CSV_LINE_TOKENIZER_INIT.statusValue()));
        DelimitedLineTokenizer filmCsvImportFormatLineTokenizer = new DelimitedLineTokenizer();
        filmCsvImportFormatLineTokenizer.setDelimiter(";");
        filmCsvImportFormatLineTokenizer.setNames(ExcelFilmHandler.EXCEL_HEADER_TAB);
        filmCsvImportFormatLineTokenizer.setStrict(false);
        watch.stop();
        jmsTemplate.convertAndSend(topic, new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_TOKENIZER_COMPLETED, null,watch.getTime(),JmsStatus.FILM_CSV_LINE_TOKENIZER_COMPLETED.statusValue()));
		logger.debug("createFilmCsvImportFormatLineTokenizer Time Elapsed: " + watch.getTime());
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
                .<FilmCsvImportFormat, Film>chunk(chunkSize)
                .reader(reader(null))
                .processor(filmProcessor())
                .writer(filmWriter())
                .build();
    }
}
