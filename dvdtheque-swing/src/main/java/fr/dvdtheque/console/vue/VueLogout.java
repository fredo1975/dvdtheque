package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.image.utils.ImageFactory;

public class VueLogout extends BaseVueAppli {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(VueLogout.class);
	private JLabel jLabel1 = new JLabel();
	protected JPanel pan;

	public VueLogout() {
		String methodName = "VueLogout constructor ";
		logger.info(methodName + "start");
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			init();
		} catch (Exception ex) {
			throw new RuntimeException(ex.toString());
		}
		logger.info(methodName + "end");
	}
	private void init() throws Exception {
		String methodName = "init ";
		logger.info(methodName + "start");
		contentPane = (JPanel) this.getContentPane();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		pan = new JPanel() {
			// Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(pref.width, pref.height);
			}
		};
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		
		ImageFactory imageFactory = new ImageFactory(contentPane,super.getImagePath());
		
		pan.add(imageFactory.getHeaderPan());
		//pan.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		pan.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		panel.add(pan);
		jLabel1 = new JLabel("Vous n'êtes plus authentifié");
		
		jLabel1.setFont(new Font(jLabel1.getFont().getFamily(), Font.BOLD, 15));
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.PAGE_AXIS));
		bodyPanel.add(jLabel1);
		//bodyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		bodyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		panel.add(bodyPanel);
		
		//panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		
		contentPane.add(panel);
		logger.info(methodName + "end");
	}
	@Override
	public void affiche() {
		String methodName = "affiche ";
		logger.info(methodName + "start");
		//jLabel1.setText(getSession().getMessage());
		jMenuItemNouveauFilm.setVisible(false);
		super.affiche();
		logger.info(methodName + "end");
	}
}
