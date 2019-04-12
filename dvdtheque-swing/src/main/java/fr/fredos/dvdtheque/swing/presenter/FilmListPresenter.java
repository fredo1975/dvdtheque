package fr.fredos.dvdtheque.swing.presenter;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.swing.view.listener.FilmListViewListener;
import fr.fredos.dvdtheque.swing.views.FilmListView;


public class FilmListPresenter implements FilmListViewListener {
	protected final Log logger = LogFactory.getLog(FilmListPresenter.class);
	@Autowired
	private FilmListView filmListView;
	
	@PostConstruct
	protected void init() {
		this.filmListView.addFilmListViewListener(this);
	}
	public void printFilmTableList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
    	logger.info("printFilmTableList ...");
    	filmListView.printFilmTableList();
    }
}
