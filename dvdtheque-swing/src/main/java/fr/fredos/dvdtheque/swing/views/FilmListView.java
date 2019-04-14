package fr.fredos.dvdtheque.swing.views;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
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
import fr.fredos.dvdtheque.swing.model.FilmTableModel;

public class FilmListView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(FilmListView.class);
	private JScrollPane scrollPane;
	@Autowired
	private JTable filmListJTable;
	@Autowired
	private FilmTableModel filmTableModel;
	@Autowired
	private JPanel filmListViewPanel;
	@Autowired
	private JLabel nbrFilmsJLabel;
	
	@PostConstruct
	protected void init() {
		filmListJTable.setModel(filmTableModel);
		filmListViewPanel.setLayout(new BoxLayout(filmListViewPanel, BoxLayout.Y_AXIS));
		filmListViewPanel.setBorder(BorderFactory.createLineBorder(Color.black));
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
		filmListJTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		filmListJTable.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(filmListJTable);
		filmListViewPanel.add(nbrFilmsJLabel);
	}
	public void addScrollPaneToFilmListViewPanel() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		filmListViewPanel.add(scrollPane);
	}
	
}
