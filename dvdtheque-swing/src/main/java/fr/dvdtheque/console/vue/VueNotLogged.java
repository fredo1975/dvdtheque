package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VueNotLogged extends BaseVueAppli {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(VueNotLogged.class);
	private JLabel jLabel1 = new JLabel();
	JPanel pan;
	JPanel panel,panelBody = null;
	public VueNotLogged() {
		String methodName = "VueNotLogged constructor ";
		logger.info(methodName + "start");
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			init();
		} catch (Exception ex) {
			throw new RuntimeException(ex.toString());
		}
		logger.info(methodName + "end");
	}
	// Initialiser le composant
	private void init() throws Exception {
		String methodName = "init ";
		logger.info(methodName + "start");
		panel = initHeader();
		contentPane.add(panel);
		panelBody = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jLabel1 = new JLabel("Désolé nous n'avons pas trouvé de userName/password correspond à votre requête.");
		jLabel1.setFont(new Font(jLabel1.getFont().getFamily(), Font.BOLD, 15));
		panelBody.add(jLabel1);
		panel.add(panelBody);
		logger.info(methodName + "end");
	}
	@Override
	public void affiche() {
		String methodName = "affiche ";
		logger.info(methodName + "start");
		super.affiche();
		logger.info(methodName + "end");
	}
	protected void executeAction(String action) {
		String methodName = "executeAction ";
		logger.info(methodName + "action=" + action);
		// fait exécuter [action] par le contrôleur
		this.setTitle("Dvdtheque : patientez...");
		// action asynchrone - on gêle le formulaire
		this.setEnabled(false);
		// on passe la main à la classe parent
		super.execute(action);
		logger.info(methodName + "end");
	}
}
