package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;


public class MaVue extends BaseVueJFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel contentPane;
	JSpinner jSpinner1 = new JSpinner();
	JButton jButtonQuitter = new JButton();
	
	// Construire le cadre
	public MaVue() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Initialiser le composant
	private void jbInit() throws Exception {
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(null);
		this.setSize(new Dimension(220, 92));
		this.setTitle("MaVue");
		jSpinner1.setBounds(new Rectangle(21, 25, 58, 28));
		jButtonQuitter.setBounds(new Rectangle(92, 26, 86, 27));
		jButtonQuitter.setText("Quitter");
		jButtonQuitter
				.addActionListener(new MaVue_jButtonQuitter_actionAdapter(this));
		contentPane.add(jSpinner1, null);
		contentPane.add(jButtonQuitter, null);
	}

	// Redéfini, ainsi nous pouvons sortir quand la fenêtre est fermée
	protected void processWindowEvent(WindowEvent e) {
	}

	void jButtonQuitter_actionPerformed(ActionEvent e) {
		// action asynchrone - on gêle le formulaire
		this.setEnabled(false);
		// on passe l'action à la classe parent
		super.execute("quitter");
	}

	// affiche
	public void affiche() {
		// on dégêle le formulaire
		this.setEnabled(true);
		// on affiche la vue parent
		super.affiche();
	}
}
class MaVue_jButtonQuitter_actionAdapter implements java.awt.event.ActionListener {
	  MaVue adaptee;

	  MaVue_jButtonQuitter_actionAdapter(MaVue adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButtonQuitter_actionPerformed(e);
	  }
	}

