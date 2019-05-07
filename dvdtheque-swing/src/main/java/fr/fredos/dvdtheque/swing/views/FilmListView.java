package fr.fredos.dvdtheque.swing.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.dvdtheque.console.table.FilmTabCellRenderer;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
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
	private JPanel filmScrollPanePanel;
	private JPanel leftHalf;
	private JPanel rightHalf;
	GridBagLayout gridbag;
	GridBagConstraints c;
	protected static final String titreTextField = "Titre";
	protected static final String titreOTextField = "Titre Original";
	private String[] filmLabels = { "Titre", "Titre Original","Année de sortie","Zone DVD","Année DVD","Réalisateur","Acteurs","Résumé","TMDB ID","Rippé le","Rippé" };
	protected JComboBox<Integer> filmYearComboBox, filmZoneDvdComboBox, filmYearDvdComboBox,rippedComboBox;
	@PostConstruct
	protected void init() {
		filmListJTable.setModel(filmTableModel);
		
		filmListViewPanel.setLayout(new BoxLayout(filmListViewPanel, BoxLayout.LINE_AXIS));
		//filmListViewPanel.setPreferredSize(new Dimension(400,1000));
		filmListViewPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		
		leftHalf = new JPanel();
		leftHalf.setLayout(new BoxLayout(leftHalf, BoxLayout.Y_AXIS));
		leftHalf.setPreferredSize(new Dimension(400,IMAGE_HEIGHT_SIZE*2));
		leftHalf.setBorder(BorderFactory.createLineBorder(Color.black));
		
		gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHEAST;
        
        rightHalf = new JPanel();
        rightHalf.setPreferredSize(new Dimension(200,200));
        rightHalf.setLayout(gridbag);
        
		//rightHalf = new JPanel(new GridLayout(2,2));
		rightHalf.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		
		JPanel numberFilmlabelPanel = new JPanel(new FlowLayout(1,0,5));
		numberFilmlabelPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		numberFilmlabelPanel.setMaximumSize(new Dimension(200,30));
		numberFilmlabelPanel.add(nbrFilmsJLabel);
		leftHalf.add(numberFilmlabelPanel);
		
		filmListJTable.setRowHeight(IMAGE_HEIGHT_SIZE);
		filmListJTable.getColumnModel().getColumn(filmListJTable.getColumnCount()-1).setCellRenderer(new FilmTabCellRenderer());
		filmListJTable.getColumnModel().getColumn(filmListJTable.getColumnCount()-1).setMaxWidth(20);
		filmListJTable.getColumnModel().getColumn(0).setMaxWidth(IMAGE_WIDTH_SIZE);
		filmListJTable.setPreferredScrollableViewportSize(new Dimension(IMAGE_WIDTH_SIZE+20, IMAGE_HEIGHT_SIZE*2));
		filmListJTable.setFillsViewportHeight(true);
		filmListJTable.getSelectionModel().addListSelectionListener(new RowListener());
		scrollPane = new JScrollPane(filmListJTable);
		scrollPane.setAlignmentX( Component.CENTER_ALIGNMENT );
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
		//scrollPane.setPreferredSize(new Dimension(400,IMAGE_HEIGHT_SIZE*4));
		filmScrollPanePanel = new JPanel(new FlowLayout(1,0,5));
		filmScrollPanePanel.add(scrollPane);
		filmScrollPanePanel.setAlignmentX( Component.CENTER_ALIGNMENT );
		//scrollPanePanel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
	}
	public void addScrollPaneToFilmListViewPanel() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		leftHalf.add(filmScrollPanePanel);
		filmListViewPanel.add(leftHalf, BorderLayout.LINE_START);
		filmListViewPanel.add(rightHalf, BorderLayout.NORTH);
		rightHalf.removeAll();
		rightHalf.revalidate();
	}
	private void populateFilmDetails(Film film) {
		logger.info("selected film="+film.toString());
		Object[] filmValues = {film.getTitre(),film.getTitreO(),film.getAnnee(),film.getDvd().getZone(),film.getDvd().getAnnee(),film.getRealisateurs(),film.getActeurs(),film.getOverview(),film.getTmdbId(),film.getDvd().getDateRip(),film.isRipped()};
		
		for (int i = 0; i < this.filmLabels.length; i++) {
			JLabel titreTextFieldLabel = new JLabel(this.filmLabels[i] + ": ");
			titreTextFieldLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
			final JLabel titreTextValueFieldLabel;
			if(filmValues[i]!=null && filmValues[i].getClass().equals(java.util.HashSet.class)) {
				//logger.info("filmValues[i].getClass()="+filmValues[i].getClass());
				
				Set<Personne> personnes = (Set<Personne>) filmValues[i];
				StringBuilder sb = new StringBuilder("<html><body>");
				
				for(Personne personne : personnes) {
					sb.append("<p>").append(personne.getNom()).append("</p>");
				}
				sb.append("</body></html>");
				titreTextValueFieldLabel = new JLabel(sb.toString());
			}else if(filmValues[i]!=null && filmValues[i].getClass().equals(java.util.Date.class)) {
				//logger.info("filmValues[i].getClass()="+filmValues[i].getClass());
				DateFormat df = new SimpleDateFormat("dd/M/yyyy");
				String ripDate = df.format(filmValues[i]);
				titreTextValueFieldLabel = new JLabel(ripDate);
			}else if(filmValues[i]!=null){
				//logger.info("filmValues[i].getClass()="+filmValues[i].getClass());
				titreTextValueFieldLabel = new JLabel(filmValues[i].toString());
			}else {
				titreTextValueFieldLabel = new JLabel();
			}
			titreTextValueFieldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
	        c.fill = GridBagConstraints.NONE;      //reset to default
	        c.weightx = 0.0;                       //reset to default
	        c.weighty = 0.0;
	        rightHalf.add(titreTextFieldLabel, c);
	        
	        c.gridwidth = GridBagConstraints.REMAINDER;     //end row
	        c.fill = GridBagConstraints.BOTH;
	        c.weightx = 1.0;
	        rightHalf.add(titreTextValueFieldLabel, c);
		}
		filmListViewPanel.revalidate();
	}
	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			rightHalf.removeAll();
			populateFilmDetails((Film) filmTableModel.getFilmAt(filmListJTable.getSelectionModel().getLeadSelectionIndex()));
		}
	}
}
