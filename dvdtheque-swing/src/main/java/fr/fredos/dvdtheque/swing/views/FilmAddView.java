package fr.fredos.dvdtheque.swing.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import fr.fredos.dvdtheque.swing.Main;
import fr.fredos.dvdtheque.swing.SpinnerDialog;
import fr.fredos.dvdtheque.swing.model.TmdbFilmTableModel;

public class FilmAddView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(FilmAddView.class);
	private JScrollPane scrollPane;
	@Autowired
	private JPanel filmAddViewPanel;
	@Autowired
	private JTextField tmdbSearchTextField;
	@Autowired
	private SpinnerDialog spinnerDialog;
	@Autowired
	private Main main;
	@Autowired
	private JTable tmdbFilmListJTable;
	@Autowired
	private TmdbFilmTableModel tmdbFilmTableModel;
	@Autowired
	private JLabel nbrTmdbFilmsJLabel;
	@Autowired
	private JLabel savedTmdbFilmsJLabel;
	@Autowired
	private JButton addTmdbFilmButton;
	JPanel tmdbFilmScrollPanePanel;
	
	@PostConstruct
	protected void init() {
		filmAddViewPanel.setLayout(new BoxLayout(filmAddViewPanel,BoxLayout.Y_AXIS));
		//filmAddViewPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		
		JPanel textFieldWithButtonPanel = new JPanel(new FlowLayout(1,0,5));
		//textFieldWithButtonPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		textFieldWithButtonPanel.add(tmdbSearchTextField);
		JButton searchButton = new JButton("Chercher le film à ajouter");
		searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
            	spinnerDialog.setFrame(main);
    			SwingWorker<?, ?> worker = new SwingWorker<Void, Integer>() {
    				protected Void doInBackground() throws InterruptedException {
    					notifyFilmAddListeners((t, u) -> {
							try {
								t.onSearchFilmButtonClicked(u);
							} catch (RestClientException | IllegalStateException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						},event);
    					return null;
    				}

    				protected void process(List<Integer> chunks) {
    					
    				}

    				protected void done() {
    					spinnerDialog.dispose();
    				}
    			};
    			worker.execute();
    			spinnerDialog.setVisible();
            }
        });
		textFieldWithButtonPanel.add(searchButton);
		textFieldWithButtonPanel.setMaximumSize(new Dimension(800,30));
		textFieldWithButtonPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		filmAddViewPanel.add(textFieldWithButtonPanel);
		
		JPanel numberFilmlabelPanel = new JPanel(new FlowLayout(1,0,5));
		//numberFilmlabelPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		numberFilmlabelPanel.setMaximumSize(new Dimension(200,30));
		numberFilmlabelPanel.add(nbrTmdbFilmsJLabel);
		filmAddViewPanel.add(numberFilmlabelPanel);
		
		
		tmdbFilmListJTable.setModel(tmdbFilmTableModel);
		tmdbFilmListJTable.setRowHeight(IMAGE_HEIGHT_SIZE);
		tmdbFilmListJTable.setPreferredScrollableViewportSize(new Dimension(1000, 800));
		//tmdbFilmListJTable.setFillsViewportHeight(true);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-tmdbFilmListJTable.getColumnCount()).setMaxWidth(IMAGE_WIDTH_SIZE);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-6).setMaxWidth(200);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-5).setMaxWidth(200);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-4).setMaxWidth(200);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-3).setMaxWidth(200);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-2).setMaxWidth(180);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-1).setMaxWidth(50);
		
		scrollPane = new JScrollPane(tmdbFilmListJTable);
		//scrollPane.setPreferredSize(new Dimension(1200,1000));
		//scrollPane.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
		tmdbFilmScrollPanePanel = new JPanel(new FlowLayout(1,0,5));
		tmdbFilmScrollPanePanel.add(scrollPane);
		scrollPane.setAlignmentX( Component.CENTER_ALIGNMENT );
		tmdbFilmScrollPanePanel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JPanel addButtonPanel = new JPanel(new FlowLayout(1,0,5));
		//addButtonPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		
		addTmdbFilmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
            	spinnerDialog.setFrame(main);
    			SwingWorker<?, ?> worker = new SwingWorker<Void, Integer>() {
    				protected Void doInBackground() throws InterruptedException {
    					notifyFilmAddListeners((t, u) -> {
							try {
								t.onAddFilmButtonClicked(u);
							} catch (RestClientException | IllegalStateException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						},event);
    					return null;
    				}

    				protected void process(List<Integer> chunks) {
    					
    				}

    				protected void done() {
    					spinnerDialog.dispose();
    				}
    			};
    			worker.execute();
    			spinnerDialog.setVisible();
            }
        });
		addTmdbFilmButton.setVisible(false);
		addButtonPanel.add(addTmdbFilmButton);
		addButtonPanel.setMaximumSize(new Dimension(800,30));
		addButtonPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		filmAddViewPanel.add(addButtonPanel);
		
		JPanel savedFilmlabelPanel = new JPanel(new FlowLayout(1,0,5));
		//savedFilmlabelPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		savedFilmlabelPanel.setMaximumSize(new Dimension(200,30));
		savedFilmlabelPanel.add(savedTmdbFilmsJLabel);
		filmAddViewPanel.add(savedFilmlabelPanel);
		
	}
	public void addScrollPaneToTmdbFilmListViewPanel() {
		filmAddViewPanel.add(tmdbFilmScrollPanePanel);
		addTmdbFilmButton.setVisible(true);
	}
	public void removeScrollPaneToTmdbFilmListViewPanel() {
		filmAddViewPanel.remove(tmdbFilmScrollPanePanel);
		filmAddViewPanel.revalidate();
	}
}
