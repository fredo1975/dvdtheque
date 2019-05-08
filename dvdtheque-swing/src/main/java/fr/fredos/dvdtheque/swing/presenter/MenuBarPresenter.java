package fr.fredos.dvdtheque.swing.presenter;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.swing.view.listener.MenuViewListener;
import fr.fredos.dvdtheque.swing.views.MenuBarView;

public class MenuBarPresenter implements MenuViewListener{
	protected final Log logger = LogFactory.getLog(MenuBarPresenter.class);
	@Autowired
	private MenuBarView menuBarView;
	@Autowired
	private FilmListPresenter filmListPresenter;
	@Autowired
	private FilmAddPresenter filmAddPresenter;
	
	@PostConstruct
	protected void init() {
		this.menuBarView.addMenuViewListener(this);
	}
	
	@Override
	public void onQuitMenuChoosed(ActionEvent evt) {
		logger.info("Application terminée...");
	    System.exit(0);
	}

	@Override
	public void onFilmListMenuChoosed(ActionEvent evt) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		logger.info("onFilmListMenuChoosed ...");
		filmListPresenter.handleFilmTableList();
	}

	@Override
	public void onAddFilm(ActionEvent evt) {
		logger.info("onAddFilm ...");
		filmAddPresenter.printFilmAddScreen();
	}
}
