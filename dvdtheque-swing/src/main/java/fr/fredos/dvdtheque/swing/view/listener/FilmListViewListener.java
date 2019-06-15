package fr.fredos.dvdtheque.swing.view.listener;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface FilmListViewListener {
	public void onUpdateFilmButtonClicked(ActionEvent evt) throws JsonProcessingException, RestClientException, IllegalStateException, IOException;
}
