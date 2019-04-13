package fr.fredos.dvdtheque.swing.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
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
	JTextField tmdbSearchTextField;
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
	
	@PostConstruct
	protected void init() {
		filmAddViewPanel.setLayout(new BoxLayout(filmAddViewPanel,BoxLayout.Y_AXIS));
		filmAddViewPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel addPanel = new JPanel(new FlowLayout());
		addPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		addPanel.add(tmdbSearchTextField);
		JButton searchButton = new JButton("Chercher sur TMBD");
		searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
            	spinnerDialog.setFrame(main);
    			SwingWorker<?, ?> worker = new SwingWorker<Void, Integer>() {
    				protected Void doInBackground() throws InterruptedException {
    					notifyFilmAddListeners((t, u) -> {
							try {
								t.onSearchButtonClicked(u);
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
		addPanel.add(searchButton);
		//addPanel.setPreferredSize(new Dimension(200,50));
		addPanel.setMaximumSize(new Dimension(900,100));
		
		filmAddViewPanel.add(addPanel);
		filmAddViewPanel.add(nbrTmdbFilmsJLabel);
		tmdbFilmListJTable.setModel(tmdbFilmTableModel);
		
		//filmAddViewPanel.setLayout(new BoxLayout(filmAddViewPanel, BoxLayout.Y_AXIS));
		
		tmdbFilmListJTable.setRowHeight(IMAGE_HEIGHT_SIZE);
		
		//tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-1).setCellRenderer(new FilmTabCellRenderer());
		//tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-1).setMaxWidth(20);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-tmdbFilmListJTable.getColumnCount()).setMaxWidth(IMAGE_WIDTH_SIZE);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-6).setMaxWidth(250);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-5).setMaxWidth(250);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-4).setMaxWidth(250);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-3).setMaxWidth(250);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-2).setMaxWidth(50);
		tmdbFilmListJTable.getColumnModel().getColumn(tmdbFilmListJTable.getColumnCount()-1).setMaxWidth(100);
		scrollPane = new JScrollPane(tmdbFilmListJTable);
		
	}
	public void addScrollPaneToTmdbFilmListViewPanel() {
		filmAddViewPanel.add(scrollPane);
	}
	public void removeScrollPaneToTmdbFilmListViewPanel() {
		filmAddViewPanel.remove(scrollPane);
	}
}
