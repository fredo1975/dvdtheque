package fr.fredos.dvdtheque.batch.configuration;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
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
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.batch.film.processor.FilmProcessor;
import fr.fredos.dvdtheque.batch.film.writer.DbFilmWriter;
import fr.fredos.dvdtheque.batch.film.writer.ExcelStreamFilmWriter;
import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.common.jms.model.JmsStatusMessage;

@Configuration
public class BatchImportFilmsConfiguration{
	protected Logger logger = LoggerFactory.getLogger(BatchImportFilmsConfiguration.class);
	private static String DVDTHEQUE_SERVICE_URL ="dvdtheque-service.url";
	private static String DVDTHEQUE_SERVICE_CLEAN_ALL ="dvdtheque-service.cleanAllFilms";
	@Autowired
	private Environment 											environment;
    @Autowired
    private JobRepository											jobRepository;
    @Autowired
    private PlatformTransactionManager 								transactionManager;
    @Autowired
    @Qualifier("rippedFlagTasklet")
    private Tasklet 												rippedFlagTasklet;
    @Autowired
    @Qualifier("retrieveDateInsertionTasklet")
    private Tasklet 												retrieveDateInsertionTasklet;
    private JmsMessageSender										jmsMessageSender;
    @Autowired
	public void setJmsMessageSender(JmsMessageSender jmsMessageSender) {
		this.jmsMessageSender = jmsMessageSender;
	}
	class DvdthequeJobResultListener implements JobExecutionListener{
		@Override
		public void beforeJob(JobExecution jobExecution) {
			//logger.debug("beforeJob");
			jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.IMPORT_INIT, null,0l,JmsStatus.IMPORT_INIT.statusValue()));
		}

		@Override
		public void afterJob(JobExecution jobExecution) {
			long executionTime = jobExecution.getEndTime().getNano()-jobExecution.getStartTime().getNano();
			logger.debug("afterJob executionTime="+executionTime/100 + " s");
			if( jobExecution.getStatus() == BatchStatus.COMPLETED ){
				jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.IMPORT_COMPLETED_SUCCESS, null,executionTime,JmsStatus.IMPORT_COMPLETED_SUCCESS.statusValue()));
			}else if(jobExecution.getStatus() == BatchStatus.FAILED){
				jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.IMPORT_COMPLETED_ERROR, null,executionTime,JmsStatus.IMPORT_COMPLETED_ERROR.statusValue()));
			}
		}
	}
   
    
    @Bean
	protected Tasklet cleanDBTasklet() {
    	return new Tasklet() {
    		@Autowired
    	    RestTemplate 													restTemplate;
			@Autowired
			AuthorizedClientServiceOAuth2AuthorizedClientManager 			authorizedClientServiceAndManager;
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				StopWatch watch = new StopWatch();
				watch.start();
				jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_INIT, null,0l,JmsStatus.CLEAN_DB_INIT.statusValue()));
				OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
						.principal("batch")
						.build();
				OAuth2AuthorizedClient authorizedClient = this.authorizedClientServiceAndManager.authorize(authorizeRequest);
				OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.add("Authorization", "Bearer " + accessToken.getTokenValue());
		        HttpEntity<?> request = new HttpEntity(headers);
				restTemplate.exchange(environment.getRequiredProperty(DVDTHEQUE_SERVICE_URL)+environment.getRequiredProperty(DVDTHEQUE_SERVICE_CLEAN_ALL), 
						HttpMethod.PUT, 
						request, 
						Void.class);
				watch.stop();
				jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.CLEAN_DB_COMPLETED, null,watch.getTime(),JmsStatus.CLEAN_DB_COMPLETED.statusValue()));
				logger.debug("database cleaning Time Elapsed: " + watch.getTime());
				return RepeatStatus.FINISHED;
			}
		};
	}
    
	@Bean(name = "importFilmsJob")
	public Job importFilmsJob() throws Exception {
		//logger.info("######## importFilmsJob");
		return new JobBuilder("importFilms", jobRepository)
				.listener(new DvdthequeJobResultListener())
				.incrementer(new RunIdIncrementer())
				.start(cleanDBStep())
				.next(importFilmsStep())
				.next(setRippedFlagStep())
				.next(setRetrieveDateInsertionStep())
				.build();
	}
	
    @Bean
    protected Step cleanDBStep() {
    	return new StepBuilder("cleanDBStep", jobRepository)
    			.tasklet(cleanDBTasklet(), transactionManager).build();
    }
    @Bean
    protected Step setRippedFlagStep() {
    	return new StepBuilder("rippedFlagStep", jobRepository)
    			.tasklet(rippedFlagTasklet, transactionManager).build();
    }
    @Bean
    protected Step setRetrieveDateInsertionStep() {
    	return new StepBuilder("retrieveDateInsertionStep", jobRepository)
    			.tasklet(retrieveDateInsertionTasklet, transactionManager).build();
    }
    @Bean
    @StepScope
    public FlatFileItemReader<FilmCsvImportFormat> reader(@Value("#{jobParameters[INPUT_FILE_PATH]}") String inputFilePath) {
    	StopWatch watch = new StopWatch();
		watch.start();
		jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.FILE_ITEM_READER_INIT, null,0l,JmsStatus.FILE_ITEM_READER_INIT.statusValue()));
    	FlatFileItemReader<FilmCsvImportFormat> csvFileReader = new FlatFileItemReader<>();
    	csvFileReader.setResource(new FileSystemResource(inputFilePath));
        csvFileReader.setLinesToSkip(1);
        LineMapper<FilmCsvImportFormat> filmCsvImportFormatLineMapper = createFilmCsvImportFormatLineMapper();
        csvFileReader.setLineMapper(filmCsvImportFormatLineMapper);
        watch.stop();
        jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.FILE_ITEM_READER_COMPLETED, null,watch.getTime(),JmsStatus.FILE_ITEM_READER_COMPLETED.statusValue()));
		logger.debug("reader Time Elapsed: " + watch.getTime());
        return csvFileReader;
    }
    
    private LineMapper<FilmCsvImportFormat> createFilmCsvImportFormatLineMapper() {
    	StopWatch watch = new StopWatch();
		watch.start();
		jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_MAPPER_INIT, null,0l,JmsStatus.FILM_CSV_LINE_MAPPER_INIT.statusValue()));
        DefaultLineMapper<FilmCsvImportFormat> filmLineMapper = new DefaultLineMapper<>();
        LineTokenizer filmCsvImportFormatLineTokenizer = createFilmCsvImportFormatLineTokenizer();
        filmLineMapper.setLineTokenizer(filmCsvImportFormatLineTokenizer);
        FieldSetMapper<FilmCsvImportFormat> filmCsvImportFormatInformationMapper = createFilmCsvImportFormatInformationMapper();
        filmLineMapper.setFieldSetMapper(filmCsvImportFormatInformationMapper);
        watch.stop();
        jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_MAPPER_COMPLETED, null,watch.getTime(),JmsStatus.FILM_CSV_LINE_MAPPER_COMPLETED.statusValue()));
		logger.debug("createFilmCsvImportFormatLineMapper Time Elapsed: " + watch.getTime());
        return filmLineMapper;
    }

    private LineTokenizer createFilmCsvImportFormatLineTokenizer() {
    	StopWatch watch = new StopWatch();
		watch.start();
		jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_TOKENIZER_INIT, null,0l,JmsStatus.FILM_CSV_LINE_TOKENIZER_INIT.statusValue()));
        DelimitedLineTokenizer filmCsvImportFormatLineTokenizer = new DelimitedLineTokenizer();
        filmCsvImportFormatLineTokenizer.setDelimiter(";");
        filmCsvImportFormatLineTokenizer.setNames(ExcelStreamFilmWriter.EXCEL_HEADER_TAB);
        filmCsvImportFormatLineTokenizer.setStrict(false);
        watch.stop();
        jmsMessageSender.sendMessage(new JmsStatusMessage<Film>(JmsStatus.FILM_CSV_LINE_TOKENIZER_COMPLETED, null,watch.getTime(),JmsStatus.FILM_CSV_LINE_TOKENIZER_COMPLETED.statusValue()));
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
    	return new StepBuilder("importFilmsStep", jobRepository)
    			.<FilmCsvImportFormat, Film>chunk(50,transactionManager)
                .reader(reader(null))
                .processor(filmProcessor())
                .writer(filmWriter())
                .build();
    }
}
