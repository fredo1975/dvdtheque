package fr.fredos.dvdtheque.swing.views;

import java.awt.CardLayout;
import java.awt.Label;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FilmAddView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(FilmAddView.class);
	final static String FILM_ADD_VIEW_PANEL = "Card with Film add";
	@Autowired
	private JPanel subPanel;
	@Autowired
	private JPanel filmAddViewPanel;
	@PostConstruct
	protected void init() {
		Label l = new Label("test");
		filmAddViewPanel.add(l);
		subPanel.add(filmAddViewPanel,FILM_ADD_VIEW_PANEL);
	}
	
	public void printFilmAddScreen() {
		logger.info("printFilmAddScreen");
		CardLayout cl = (CardLayout)(subPanel.getLayout());
        cl.show(subPanel, FILM_ADD_VIEW_PANEL);
	}
}
