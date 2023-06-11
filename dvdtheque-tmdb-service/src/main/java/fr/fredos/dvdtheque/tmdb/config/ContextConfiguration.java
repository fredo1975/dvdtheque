package fr.fredos.dvdtheque.tmdb.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ContextConfiguration {
	@Bean
    public RestTemplate restTemplate() {
		return new RestTemplateBuilder()
	            .requestFactory(this::requestFactory)
	            .build();
    }
	private HttpComponentsClientHttpRequestFactory requestFactory() {

        RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectionRequestTimeout(200000)
            .setSocketTimeout(200000)
            .setConnectTimeout(200000)
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
