package fr.fredos.dvdtheque.rest.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ContextConfiguration {
	@Bean
    public RestTemplate restTemplate() {
		RestTemplate rest = new RestTemplate();
		rest.getInterceptors().add((request, body, execution) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null) {
				return execution.execute(request, body);
			}

			if (!(authentication.getCredentials() instanceof AbstractOAuth2Token)) {
				return execution.execute(request, body);
			}

			AbstractOAuth2Token token = (AbstractOAuth2Token) authentication.getCredentials();
		    request.getHeaders().setBearerAuth(token.getTokenValue());
		    return execution.execute(request, body);
		});
		rest.setRequestFactory(requestFactory());
		return rest;
    }
	
	private HttpComponentsClientHttpRequestFactory requestFactory() {

        RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectionRequestTimeout(2000000)
            .setSocketTimeout(2000000)
            .setConnectTimeout(2000000)
            .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(5);
        connectionManager.setDefaultMaxPerRoute(5);
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                                                          .setConnectionManager(connectionManager)
                                                          .setDefaultRequestConfig(requestConfig)
                                                          .build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
