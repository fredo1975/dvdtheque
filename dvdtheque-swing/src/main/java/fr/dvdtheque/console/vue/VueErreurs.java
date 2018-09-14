package fr.dvdtheque.console.vue;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VueErreurs extends BaseVueAppli {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(VueErreurs.class);
	JPanel jPanel,pan;
	JLabel jLabel1 = new JLabel();
	JScrollPane jScrollPane1 = new JScrollPane();
	JTextPane jTextPaneErreurs = new JTextPane();

	// Construire le cadre
	public VueErreurs() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() throws Exception {
		String methodName = "init ";
		logger.info(methodName + "start");
		/*
		jPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pan = new JPanel();
		pan.setBorder(BorderFactory.createTitledBorder("Film"));
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		pan.setAlignmentX(Component.CENTER_ALIGNMENT);
		pan.setAlignmentY(Component.CENTER_ALIGNMENT);
		contentPane = (JPanel) this.getContentPane();
		*/
		jPanel = initHeader();
		
		logger.info(methodName + "end");
	}

	public void affiche() {
		// affichage des erreurs dans le jTextPane
		pan.removeAll();
		pan.updateUI();
		ArrayList<String> erreurs = getSession().getErreurs();
		String msg = "";
		for (int i = 0; i < erreurs.size(); i++) {
			msg += (i + 1) + " - " + erreurs.get(i);
		}
		jTextPaneErreurs.setText(msg);
		pan.add(jTextPaneErreurs);
		pan.setOpaque(true);
		jPanel.add(pan);
		this.getContentPane().add(jPanel);
		
		// affichage parent
		super.affiche();
	}
}
