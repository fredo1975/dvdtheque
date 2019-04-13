package fr.fredos.dvdtheque.swing.presenter;

import java.awt.CardLayout;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.swing.model.FilmTableModel;
import fr.fredos.dvdtheque.swing.view.listener.FilmListViewListener;
import fr.fredos.dvdtheque.swing.views.FilmListView;


public class FilmListPresenter implements FilmListViewListener {
	protected final Log logger = LogFactory.getLog(FilmListPresenter.class);
	final static String FILM_LIST_VIEW_PANEL = "Card with Film list";
	@Autowired
	private FilmListView filmListView;
	@Autowired
	private JPanel subPanel;
	@Autowired
	private JPanel filmListViewPanel;
	@Autowired
	private FilmTableModel filmTableModel;
	@Autowired
	private JLabel nbrFilmsJLabel;
	@PostConstruct
	protected void init() {
		this.filmListView.addFilmListViewListener(this);
		subPanel.add(filmListViewPanel,FILM_LIST_VIEW_PANEL);
	}
	public void handleFilmTableList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
    	logger.info("handleFilmTableList ...");
    	filmTableModel.buildFilmList();
    	filmListView.addScrollPaneToFilmListViewPanel();
    	nbrFilmsJLabel.setText("Nombre de films : "+String.valueOf(filmTableModel.getRowCount()));
    	CardLayout cl = (CardLayout)(subPanel.getLayout());
        cl.show(subPanel, FILM_LIST_VIEW_PANEL);
        subPanel.revalidate();
    }
}
