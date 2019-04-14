package fr.fredos.dvdtheque.swing.presenter;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.swing.model.TmdbFilmTableModel;
import fr.fredos.dvdtheque.swing.service.FilmRestService;
import fr.fredos.dvdtheque.swing.view.listener.FilmAddViewListener;
import fr.fredos.dvdtheque.swing.views.FilmAddView;

public class FilmAddPresenter implements FilmAddViewListener{
	protected final Log logger = LogFactory.getLog(FilmAddPresenter.class);
	final static String FILM_ADD_VIEW_PANEL = "Card with Film add";
	@Autowired
	private FilmAddView filmAddView;
	@Autowired
	private JPanel subPanel;
	@Autowired
	private JPanel filmAddViewPanel;
	@Autowired
	private FilmRestService filmRestService;
	@Autowired
	JTextField tmdbSearchTextField;
	@Autowired
	private TmdbFilmTableModel tmdbFilmTableModel;
	@Autowired
	private JLabel nbrTmdbFilmsJLabel;
	
	@PostConstruct
	protected void init() {
		this.filmAddView.addFilmAddViewListener(this);
		subPanel.add(filmAddViewPanel,FILM_ADD_VIEW_PANEL);
	}
	public void printFilmAddScreen() {
    	logger.info("printFilmAddScreen ...");
    	tmdbSearchTextField.setText("replicant");
    	tmdbFilmTableModel.clearFilmSet();
    	//nbrTmdbFilmsJLabel.setText(null);
    	nbrTmdbFilmsJLabel.setVisible(false);
    	//filmAddView.removeScrollPaneToTmdbFilmListViewPanel();
    	CardLayout cl = (CardLayout)(subPanel.getLayout());
        cl.show(subPanel, FILM_ADD_VIEW_PANEL);
        subPanel.revalidate();
    }

	@Override
	public void onSearchButtonClicked(ActionEvent evt) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		logger.info("onSearchButtonClicked tmdbSearchTextField="+tmdbSearchTextField.getText());
		if(StringUtils.isEmpty(tmdbSearchTextField.getText())) {
			JOptionPane.showMessageDialog(filmAddViewPanel, "il faut entrer un titre de film", "Chercher", JOptionPane.WARNING_MESSAGE);
		}else {
			Set<Film> films = filmRestService.findTmdbFilmByTitre(tmdbSearchTextField.getText());
			logger.info("onSearchButtonClicked films="+films.toString());
			tmdbFilmTableModel.populateFilmSet(films);
			nbrTmdbFilmsJLabel.setText("Nombre de films : "+String.valueOf(tmdbFilmTableModel.getRowCount()));
			nbrTmdbFilmsJLabel.setVisible(true);
			filmAddView.addScrollPaneToTmdbFilmListViewPanel();
		}
	}
}
