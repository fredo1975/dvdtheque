package fr.dvdtheque.console.vue;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.swing.model.FilmTableModel;

public class VueListeFilm extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(VueListeFilm.class);
	// les composants de la vue
	private JLabel jLabel1 = new JLabel();
	JPanel jPanel;
	JPanel panel;
	private JScrollPane jScrollPane1;
	private JTable jTableFilm = new JTable();
	private JLabel jLabelMessage = new JLabel();
	List<Film> filmList;
	public VueListeFilm() {
		
	}
	public VueListeFilm(List<Film> filmList) {
		this.filmList=filmList;
	}

	// Initialiser le composant
	public JPanel init() throws Exception {
		String methodName = "init ";
		logger.info(methodName + "start");
		
		initTableFilm();
		JPanel tablePanel = new JPanel();
		jScrollPane1 = new JScrollPane(jTableFilm);
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = this.getSize();
			logger.info(methodName + "screenSize=" + screenSize);
			logger.info(methodName + "frameSize=" + frameSize);
			Double w = new Double(screenSize.getWidth());
			Double h = new Double(screenSize.getHeight());
			logger.info(methodName + "w=" + w.intValue() + " h=" + h.intValue());
			jScrollPane1.setPreferredSize(new Dimension(w.intValue() / 2, h.intValue() / 2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		tablePanel.add(jScrollPane1);
		return (JPanel) this.add(tablePanel);
		//panel.add(tablePanel);
		//contentPane.add(panel);
		//logger.info(methodName + "end");
		//return tablePanel;
		
	}

	public void initTableFilm() {
		//jTableFilm.addMouseListener(new TableListe_mouseAdapter(this));
		jTableFilm.setRowSelectionAllowed(false);
		jTableFilm.setColumnSelectionAllowed(false);
		jTableFilm.setModel(new FilmTableModel(getFilmList()));
	}

	

	// gestion du clic sur la table des articles
	void table_mouseClicked(MouseEvent e) {
		String methodName = "table_mouseClicked ";
		logger.info(methodName + "start");
		// la colonne sélectionnée
		int col = jTableFilm.getSelectedColumn();
		// on ne s'interesse qu'à la colonne no 2
		if (col != jTableFilm.getColumnCount() - 1) {
			return;
		}
		/*
		// on note l'identité de l'article
		Film film = (Film) (getSession().getFilmList().get(jTableFilm
				.getSelectedRow()));
		logger.info(methodName + "film=" + film.toString());
		getSession().setFilmId(film.getId());
		getSession().setFilm(film);
		// on fait exécuter l'action
		super.exécuteAction("filminfos");*/
		logger.info(methodName + "end");
	}

	// Redéfini, ainsi nous pouvons sortir quand la fenêtre est fermée
	protected void processWindowEvent(WindowEvent e) {
	}
	
	public List<Film> getFilmList() {
		return filmList;
	}

	public void setFilmList(List<Film> filmList) {
		this.filmList = filmList;
	}

	
}
/*
class TableListe_mouseAdapter extends java.awt.event.MouseAdapter {
	VueListeFilm adaptee;

	TableListe_mouseAdapter(VueListeFilm adaptee) {
		this.adaptee = adaptee;
	}

	public void mouseClicked(MouseEvent e) {
		adaptee.table_mouseClicked(e);
	}
}*/