package fr.fredos.dvdtheque.swing.views;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.dvdtheque.console.table.FilmTabCellRenderer;
import fr.fredos.dvdtheque.swing.Main;
import fr.fredos.dvdtheque.swing.model.FilmTableModel;

public class FilmListView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(FilmListView.class);
	final static String FILM_LIST_VIEW_PANEL = "Card with Film list";
	@Autowired
	private JTable filmListJTable;
	@Autowired
	private FilmTableModel filmTableModel;
	@Autowired
	private Main main;
	@Autowired
	private JPanel subPanel;
	@Autowired
	private JPanel filmListViewPanel;
	private JScrollPane scrollPane;
	
	@PostConstruct
	protected void init() {
		filmListJTable.setModel(filmTableModel);
		/*
		try{
			Double w = new Double(main.getScreenSize().getWidth())/1.2;
			Double h = new Double(main.getScreenSize().getHeight())/1.5;
			scrollPane.setPreferredSize(new Dimension(w.intValue(),h.intValue()));
		}catch(Exception e){
			e.printStackTrace();
		}*/
		filmListJTable.setRowHeight(IMAGE_HEIGHT_SIZE);
		filmListJTable.getColumnModel().getColumn(filmListJTable.getColumnCount()-1).setCellRenderer(new FilmTabCellRenderer());
		filmListJTable.getColumnModel().getColumn(filmListJTable.getColumnCount()-1).setMaxWidth(20);
		filmListJTable.getColumnModel().getColumn(0).setMaxWidth(IMAGE_WIDTH_SIZE);
		scrollPane = new JScrollPane(filmListJTable);
		subPanel.add(filmListViewPanel,FILM_LIST_VIEW_PANEL);
	}
	public void printFilmTableList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		filmTableModel.buildFilmList();
		filmListViewPanel.add(scrollPane);
		CardLayout cl = (CardLayout)(subPanel.getLayout());
        cl.show(subPanel, FILM_LIST_VIEW_PANEL);
        subPanel.revalidate();
	}
}
