package fr.fredos.dvdtheque.swing.views;

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

import fr.fredos.dvdtheque.swing.Main;
import fr.fredos.dvdtheque.swing.model.FilmTableModel;

public class FilmListView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(FilmListView.class);
	@Autowired
	private JTable filmListJTable;
	@Autowired
	private FilmTableModel filmTableModel;
	@Autowired
	private Main main;
	@Autowired
	private JPanel contentPane;
	private JPanel subPanel;
	private JScrollPane scrollPane;
	
	@PostConstruct
	protected void init() {
		filmListJTable.setModel(filmTableModel);
		filmListJTable.setPreferredScrollableViewportSize(new Dimension(800, 200));
		filmListJTable.setFillsViewportHeight(true);
		filmListJTable.setRowHeight(800);
		scrollPane = new JScrollPane(filmListJTable);
		subPanel = new JPanel();
	}
	public void printFilmTableList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		filmTableModel.buildFilmList();
		
		try{
			Double w = new Double(main.getScreenSize().getWidth());
			Double h = new Double(main.getScreenSize().getHeight());
			scrollPane.setPreferredSize(new Dimension(w.intValue()/2,h.intValue()/2));
		}catch(Exception e){
			//getSession().getErreurs().add(e.getMessage());
			e.printStackTrace();
		}
		subPanel.add(scrollPane);
		contentPane.remove(subPanel);
		contentPane.add(subPanel);
		contentPane.revalidate();
	}
}
