package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VueHome extends BaseVueAppli {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final Log logger = LogFactory.getLog(VueHome.class);
	
	// les composants de la vue
	private JPanel contentPane;
	JPanel pan;
	private JLabel jLabel1;
	protected JPanel jPanel1 = new JPanel();
	public VueHome() {
		String methodName = "VueHome constructor ";
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
		
		JPanel panel = initHeader();
		jLabel1 = new JLabel("DVDTHEQUE");
		jLabel1.setFont(new Font(jLabel1.getFont().getFamily(), Font.BOLD, 15));
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.PAGE_AXIS));
		bodyPanel.add(jLabel1);
		//bodyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		bodyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		panel.add(bodyPanel);
		
		//panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		contentPane.add(panel);
		//contentPane.add(jTableDvd);
		logger.info(methodName + "end");
	}
	
}
