package fr.fredos.dvdtheque.batch.configuration;

import java.util.List;
import java.util.Objects;

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
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.batch.film.writer.ExcelStreamFilmWriter;
import fr.fredos.dvdtheque.batch.model.Film;

@Configuration
@EnableBatchProcessing
@Lazy
public class BatchExportFilmsConfiguration {
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfiguration.class);
	@Autowired
	protected JobBuilderFactory 									jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory 									stepBuilderFactory;
    @Autowired
    RestTemplate													restTemplate;
    @Autowired
    private Environment 											environment;
    @Autowired
	private AuthorizedClientServiceOAuth2AuthorizedClientManager 	authorizedClientServiceAndManager;

    public static String 							DVDTHEQUE_SERVICE_URL="dvdtheque-service.url";
	public static String 							DVDTHEQUE_SERVICE_ALL="dvdtheque-service.films";
	
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Job exportFilmsJob() {
    	logger.info("########### exportFilmsJob");
    	return jobBuilderFactory.get("exportFilms")
				.incrementer(new RunIdIncrementer())
				.start(exportFilmsStep())
				.build();
	}
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    protected ListItemReader<Film> dvdthequeServiceFilmReader() {
    	logger.info("########### ListItemReader");
    	OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
				.principal("batch")
				.build();

		// Perform the actual authorization request using the authorized client service and authorized client
		// manager. This is where the JWT is retrieved from the Okta servers.
		OAuth2AuthorizedClient authorizedClient = this.authorizedClientServiceAndManager.authorize(authorizeRequest);

		// Get the token from the authorized client object
		OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
/*
		logger.info("Issued: " + accessToken.getIssuedAt().toString() + ", Expires:" + accessToken.getExpiresAt().toString());
		logger.info("Scopes: " + accessToken.getScopes().toString());
		logger.info("Token: " + accessToken.getTokenValue());
*/
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken.getTokenValue());
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<List<Film>> filmList = restTemplate.exchange(environment.getRequiredProperty(DVDTHEQUE_SERVICE_URL)+environment.getRequiredProperty(DVDTHEQUE_SERVICE_ALL)+"?displayType=TOUS"
    			/*"http://localhost:8762/dvdtheque-service/films?displayType=TOUS"*/, 
    			HttpMethod.GET, 
    			request, 
    			new ParameterizedTypeReference<List<Film>>(){});
    	
    	return new ListItemReader<>(filmList.getBody());
    	/*
    	ResponseEntity<List<Film>> filmList2 = ResponseEntity.ok(new ArrayList<>());
    	return new ListItemReader<>(filmList2.getBody());
    	*/
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    protected ExcelStreamFilmWriter excelFilmWriter() {
    	logger.info("########### excelFilmWriter");
    	return new ExcelStreamFilmWriter();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    protected Step exportFilmsStep() {
    	logger.info("########### exportFilmsStep");
        return stepBuilderFactory.get("exportFilms")
                .<Film, Film>chunk(800).reader(dvdthequeServiceFilmReader())
                .writer(excelFilmWriter())
                .build();
    }
    @Bean
    public ObjectMapper mapper() {
    	return new ObjectMapper();
    }
}
