package fr.fredos.dvdtheque.swing.views;

import java.awt.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.swing.view.listener.ViewListener;

public class MenuBarPresenter implements ViewListener{
	protected final Log logger = LogFactory.getLog(MenuBarPresenter.class);
	private final MenuBarView view;

    public MenuBarPresenter(final MenuBarView view) {
        this.view = view;
        this.view.addListener(this);
    }
	@Override
	public void onFilmListMenuChoosed(ActionEvent evt) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onQuitMenuChoosed(ActionEvent evt) {
		logger.info("Application terminée...");
	    System.exit(0);
	}
}
