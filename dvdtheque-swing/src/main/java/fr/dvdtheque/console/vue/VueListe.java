package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.table.FilmTabCellRenderer;
import fr.dvdtheque.console.table.FilmTableModel;
import fr.fredos.dvdtheque.dao.model.object.Film;

public class VueListe extends BaseVueAppli {
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(VueListe.class);
	// les composants de la vue
	JPanel jPanel;
	JPanel panel;
	private JScrollPane jScrollPane1;
	private JTable jTableFilm = new JTable();

	public VueListe() {
		String methodName = "VueListe constructor ";
		logger.debug(methodName + "start");
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			init();
		} catch (Exception ex) {
			throw new RuntimeException(ex.toString());
		}
		logger.debug(methodName + "end");
	}
	
	// Initialiser le composant
	private void init() throws Exception {
		String methodName = "init ";
		logger.debug(methodName + "start");
		JPanel panel = initHeader();
		initTableFilm();
		JPanel tablePanel = new JPanel();
		jScrollPane1 = new JScrollPane(jTableFilm);
		try{
			Double w = new Double(this.getScreenSize().getWidth());
			Double h = new Double(this.getScreenSize().getHeight());
			logger.debug(methodName + "w="+w.intValue()+" h="+h.intValue());
			jScrollPane1.setPreferredSize(new Dimension(w.intValue()/2,h.intValue()/2));
		}catch(Exception e){
			getSession().getErreurs().add(e.getMessage());
			e.printStackTrace();
		}
		tablePanel.add(jScrollPane1);
		panel.add(tablePanel);
		contentPane.add(panel);
		logger.debug(methodName + "end");
	}

	public void initTableFilm() {
		jTableFilm.addMouseListener(new TableListe_mouseAdapter(this));
		jTableFilm.setRowSelectionAllowed(true);
		jTableFilm.setColumnSelectionAllowed(false);
	}

	@Override
	public void affiche() {
		String methodName = "affiche ";
		logger.debug(methodName + "start");
		jTableFilm.setModel(new FilmTableModel(getSession().getFilmList()));
		jTableFilm.getColumnModel().getColumn(jTableFilm.getColumnCount()-1).setCellRenderer(new FilmTabCellRenderer());
		jTableFilm.getColumnModel().getColumn(jTableFilm.getColumnCount()-1).setMaxWidth(17);
		super.affiche();
		logger.debug(methodName + "end");
	}

	// gestion du clic sur la table des films
	void table_mouseClicked(MouseEvent e) {
		String methodName = "table_mouseClicked ";
		logger.debug(methodName + "start");
		// la colonne sélectionnée
		int col = jTableFilm.getSelectedColumn();
		// on ne s'interesse qu'à la colonne no 2
		if (col != jTableFilm.getColumnCount()-2) {
			return;
		}
		// on note l'identité du film
		Film film = (Film) (getSession().getFilmList().get(jTableFilm.getSelectedRow()));
		logger.debug(methodName + "film="+film.toString());
		getSession().setFilmId(film.getId());
		getSession().setFilm(film);
		// on fait exécuter l'action
		super.executeAction("filminfos");
		logger.debug(methodName + "end");
	}

	// Redéfini, ainsi nous pouvons sortir quand la fenetre est fermeée
	protected void processWindowEvent(WindowEvent e) {
	}
	
	public static void main(String[] args) {
		VueListe vueListe = new VueListe();
	}
	

}
class TableListe_mouseAdapter extends java.awt.event.MouseAdapter {
	VueListe adaptee;

	TableListe_mouseAdapter(VueListe adaptee) {
		this.adaptee = adaptee;
	}

	public void mouseClicked(MouseEvent e) {
		adaptee.table_mouseClicked(e);
	}
}