package fr.fredos.dvdtheque.swing.presenter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.fredos.dvdtheque.swing.view.listener.FilmAddViewListener;
import fr.fredos.dvdtheque.swing.views.FilmAddView;

public class FilmAddPresenter implements FilmAddViewListener{
	protected final Log logger = LogFactory.getLog(FilmAddPresenter.class);
	@Autowired
	private FilmAddView filmAddView;
	
	public void printFilmAddScreen() {
    	logger.info("printFilmAddScreen ...");
    	filmAddView.printFilmAddScreen();
    }
}
