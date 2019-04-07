package fr.fredos.dvdtheque.swing.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.swing.view.listener.FilmListViewListener;

public class FilmListPresenter implements FilmListViewListener {
	protected final Log logger = LogFactory.getLog(FilmListPresenter.class);
	private final FilmListView view;
    //private final FilmTableModel filmTableModel;
    
    public FilmListPresenter(final FilmListView view) {
        this.view = view;
        this.view.addFilmListViewListener(this);
    }

    protected void buildFilmList() {
    	logger.info("building the film list ...");
    }
}
