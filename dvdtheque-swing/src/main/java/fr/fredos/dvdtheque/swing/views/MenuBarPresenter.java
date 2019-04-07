package fr.fredos.dvdtheque.swing.views;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.swing.view.listener.MenuViewListener;

public class MenuBarPresenter implements MenuViewListener{
	protected final Log logger = LogFactory.getLog(MenuBarPresenter.class);
	private final MenuBarView view;
	private final FilmListPresenter filmListPresenter;

    public MenuBarPresenter(final MenuBarView view, final FilmListPresenter filmListPresenter) {
        this.view = view;
        this.view.addMenuViewListener(this);
        this.filmListPresenter = filmListPresenter;
    }
	
	@Override
	public void onQuitMenuChoosed(ActionEvent evt) {
		logger.info("Application terminée...");
	    System.exit(0);
	}

	@Override
	public void onFilmListMenuChoosed(ActionEvent evt) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		logger.info("building the film list ...");
		filmListPresenter.buildFilmList();
	}
}
