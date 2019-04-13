package fr.fredos.dvdtheque.swing.view.listener;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface FilmAddViewListener {
	public void onSearchButtonClicked(ActionEvent evt) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException;
}
