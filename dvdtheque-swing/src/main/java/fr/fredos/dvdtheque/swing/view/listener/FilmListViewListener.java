package fr.fredos.dvdtheque.swing.view.listener;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface FilmListViewListener {
	public void onUpdateFilmButtonClicked(ActionEvent evt) throws JsonProcessingException, RestClientException, IllegalStateException, IOException;
	public void handleFilmTableList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException;
}
