package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.factory.ImageFactory;

public class VueLogged extends BaseVueAppli{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(VueLogged.class);
	final static int GAP = 10;
	private JLabel jLabel1;
	
	JPanel pan;
	public VueLogged() {
		String methodName = "VueLogged constructor ";
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
		contentPane = (JPanel) this.getContentPane();
		
		pan = new JPanel() {
			// Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(pref.width, pref.height);
			}
		};
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		pan.setOpaque(true);
		
		ImageFactory imageFactory = new ImageFactory(contentPane,super.getImagePath());
		
		pan.add(imageFactory.getHeaderPan());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(pan);
		contentPane.add(panel);
		
		panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jLabel1 = new JLabel();
		
		panel.add(jLabel1);
		pan.add(panel);
		logger.info(methodName + "end");
	}
	@Override
	public void affiche() {
		String methodName = "affiche ";
		logger.info(methodName + "start");
		/*if(null != getSession().getUser()){
			jLabel1.setText("Bienvenue "+getSession().getUser().getFirstName()+" "+getSession().getUser().getLastName()+"\n vous êtes désormais loggé !");
			jLabel1.setFont(new Font(jLabel1.getFont().getFamily(), Font.BOLD, 15));
			
		}*/
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
