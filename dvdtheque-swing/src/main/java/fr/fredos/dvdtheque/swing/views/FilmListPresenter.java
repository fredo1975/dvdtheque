package fr.fredos.dvdtheque.swing.views;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.swing.model.FilmTableModel;
import fr.fredos.dvdtheque.swing.view.listener.FilmListViewListener;


public class FilmListPresenter implements FilmListViewListener {
	protected final Log logger = LogFactory.getLog(FilmListPresenter.class);
	private final FilmListView view;
	@Autowired
    private FilmTableModel filmTableModel;
    
    public FilmListPresenter(final FilmListView view) {
        this.view = view;
        this.view.addFilmListViewListener(this);
    }

    protected void buildFilmList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
    	logger.info("building the film list ...");
    	filmTableModel.buildFilmList();
    }
}
