package fr.fredos.dvdtheque.swing.view.listener;

import java.awt.event.ActionEvent;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface FilmListViewListener {
	public void onUpdateFilmButtonClicked(ActionEvent evt) throws JsonProcessingException;
}
