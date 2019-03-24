package fr.fredos.dvdtheque.swing.views;

import java.awt.event.ActionEvent;

import fr.fredos.dvdtheque.swing.model.FilmTableModel;
import fr.fredos.dvdtheque.swing.view.listener.ViewListener;

public class FilmListPresenter implements ViewListener {
	private final FilmListView filmListView;
    private final FilmTableModel filmTableModel;
    
    public FilmListPresenter(final FilmListView view, final FilmTableModel model) {
        this.filmListView = view;
        view.addListener(this);
        this.filmTableModel = model;
    }
	@Override
	public void onFilmListMenuChoosed(ActionEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onQuitMenuChoosed(ActionEvent evt) {
		// TODO Auto-generated method stub

	}

}
