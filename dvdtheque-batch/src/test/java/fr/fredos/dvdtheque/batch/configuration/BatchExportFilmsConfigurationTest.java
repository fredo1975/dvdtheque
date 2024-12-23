package fr.fredos.dvdtheque.batch.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.batch.model.Dvd;
import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.batch.model.Personne;
import fr.fredos.dvdtheque.common.enums.DvdFormat;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;
import jakarta.jms.Topic;


@ActiveProfiles("test-export")
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    StepScopeTestExecutionListener.class})
	@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class BatchExportFilmsConfigurationTest {
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfigurationTest.class);
	@Autowired
	@Qualifier(value = "runExportFilmsJob")
	private Job						job;
	
	@Autowired
	private RestTemplate 			restTemplate;
	
	@Autowired
	private JobRepository 			jobRepository;
	
	private Film buildfilm() {
		Film film = new Film();
		film.setAnnee(2012);
		film.setId(1l);
		film.setDvd(new Dvd());
		film.getDvd().setAnnee(2013);
		film.getDvd().setDateRip(Date.from(LocalDate.of(2013, 8, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		film.getDvd().setFormat(DvdFormat.DVD);
		film.getDvd().setRipped(true);
		film.getDvd().setZone(2);
		film.setDateInsertion(Date.from(LocalDate.of(2013, 10, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		film.setActeur(new HashSet<Personne>());
		Personne act1 = new Personne();
		act1.setId(1l);
		act1.setNom("Tom Cruise");
		film.getActeur().add(act1);
		Personne real = new Personne();
		real.setId(2l);
		real.setNom("David Lynch");
		film.setRealisateur(new HashSet<Personne>());
		film.getRealisateur().add(real);
		film.setTitre("film");
		film.setOrigine(FilmOrigine.DVD);
		film.setTmdbId(1l);
		film.setVu(false);
		return film;
	}
	@SuppressWarnings("unchecked")
	@Test
	public void launchExportFilmsJob() throws Exception {
		List<Film> l = new ArrayList<>();
		l.add(buildfilm());
        ResponseEntity<List<Film>> filmList = new ResponseEntity<List<Film>>(l,HttpStatus.ACCEPTED);
        Mockito.when(restTemplate.exchange(Mockito.any(String.class),
        		Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<?>> any(),
    			Mockito.any(ParameterizedTypeReference.class)))
        .thenReturn(filmList);
		Calendar c = Calendar.getInstance();
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("TIMESTAMP", c.getTime());
		JobParameters jobParameters = builder.toJobParameters();
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.afterPropertiesSet();
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	
	@Bean
	JmsTemplate jmsTemplate() {
		return Mockito.mock(JmsTemplate.class);
	}

	@Bean
	Topic topic() {
		return Mockito.mock(Topic.class);
	}

	@Bean
	ClientRegistration dvdthequeClientRegistration(
			@Value("${spring.security.oauth2.client.provider.keycloak.token-uri}") String token_uri,
			@Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String client_secret,
			@Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}") String authorizationGrantType) {
		return ClientRegistration.withRegistrationId("keycloak").tokenUri(token_uri).clientId("dvdtheque-api")
				.clientSecret(client_secret).authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
				.build();
	}

	@Bean
	AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager(ClientRegistration dvdthequeClientRegistration) {
		ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(
				dvdthequeClientRegistration);
		Map<OAuth2AuthorizedClientId, OAuth2AuthorizedClient> authorizedClients = new HashMap<>();
		authorizedClients.put(new OAuth2AuthorizedClientId("keycloak", "batch"),
				new OAuth2AuthorizedClient(dvdthequeClientRegistration, "batch",
						new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "cscs", Instant.now(), Instant.MAX)));
		OAuth2AuthorizedClientService authorizedClientService = new InMemoryOAuth2AuthorizedClientService(
				clientRegistrationRepository, authorizedClients);
		return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
				authorizedClientService);
	}
	
	@Bean(name = "dataSource")
	public DataSource dataSource() {
	    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
	    return builder.setType(EmbeddedDatabaseType.HSQL)
	      .addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql")
	      .addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql")
	      .build();
	}
}
