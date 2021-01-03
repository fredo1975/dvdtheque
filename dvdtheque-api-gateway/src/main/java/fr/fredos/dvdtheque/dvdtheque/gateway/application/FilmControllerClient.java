package fr.fredos.dvdtheque.dvdtheque.gateway.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

//@Component
public class FilmControllerClient {
	@Autowired
	private RestTemplate loadBalancedRestTemplate;
/*
	public List<Film> findAllFilms() throws JsonParseException, JsonMappingException, RestClientException, IOException {
		UriComponentsBuilder builder = fromHttpUrl("http://dvdtheque-service/films");
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(loadBalancedRestTemplate.getForObject(builder.toUriString(), String.class), new TypeReference<List<Film>>(){});
	}*/
}
