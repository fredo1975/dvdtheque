package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.factory.ImageFactory;

public class BaseVueAppli extends BaseVueJFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(BaseVueAppli.class);
	// les composants
	protected JPanel contentPane;
	protected JPanel jPanel,panel;
	protected JMenuBar jMenuBar1 = new JMenuBar();
	protected JMenu jMenu1 = new JMenu("Actions");
	protected JMenuItem jMenuItemLogin = new JMenuItem("Login");
	protected JMenuItem jMenuItemLogout = new JMenuItem("Logout");
	protected JMenuItem jMenuItemListeFilm = new JMenuItem("Liste des Films");
	protected JMenuItem jMenuItemNouveauFilm = new JMenuItem("Ajouter Film ou Personne");
	protected JMenuItem jMenuItemQuitter = new JMenuItem("Quitter");
	public static final String okPath="/img/ok.png";
	public static final String koPath="/img/ko.png";
	
	// session de l'application commune aux vues et actions
	
	private Dimension screenSize;
	private Dimension frameSize;
	private String imagePath = "/img/header.JPG";
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public Dimension getScreenSize() {
		return screenSize;
	}
	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
	}
	public Dimension getFrameSize() {
		return frameSize;
	}
	public void setFrameSize(Dimension frameSize) {
		this.frameSize = frameSize;
	}
	public BaseVueAppli() {
		String methodName = "BaseVueAppli constructor ";
		logger.debug(methodName + "start");
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			init();
		} catch (Exception ex) {
			throw new RuntimeException(ex.toString());
		}
		logger.debug(methodName + "end");
	}

	protected ImageIcon createImageIcon(String path, String description) throws MalformedURLException {
		//java.net.URL imgURL = getClass().getResource(path);
		java.net.URL imgURL = getURL(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	protected JPanel initHeader(){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JPanel pan = new JPanel() {
			private static final long serialVersionUID = 1L;

			// Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(pref.width, pref.height);
			}
		};
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		ImageFactory imageFactory = new ImageFactory(contentPane,getImagePath());
		pan.add(imageFactory.getHeaderPan());
		panel.add(pan);
		return panel;
	}
	
	public static URL getURL(String file) throws MalformedURLException {
		/*
		URL documentBase = new URL("file:" + System.getProperty("user.dir")
				+ "/");
		*/
		URL documentBase = BaseVueAppli.class.getResource(file);
		return new URL(documentBase, file);
	}

	// Initialiser le composant
	private void init() throws Exception {
		String methodName = "init ";
		/*
		logger.info(methodName + "start System.getProperty(user.dir)="+System.getProperty("user.dir"));
		logger.info(methodName + "start System.getProperty(java.class.path)="+System.getProperty("java.class.path"));
		*/
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize = this.getSize();
		logger.debug(methodName + "screenSize=" + screenSize);
		logger.debug(methodName + "frameSize=" + frameSize);

		contentPane = (JPanel) this.getContentPane();
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		buildMenu();
		
		logger.info(methodName + "end");
	}

	private void buildMenu() {
		jMenuItemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		jMenuItemQuitter.getAccessibleContext().setAccessibleDescription(
				"This doesn't really do anything");
		jMenuItemQuitter.setActionCommand("quitter");
		jMenuItemListeFilm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		jMenuItemListeFilm.getAccessibleContext().setAccessibleDescription(
				"This doesn't really do anything");
		jMenuItemListeFilm.setActionCommand("liste");
		jMenuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		jMenuItemLogin.setActionCommand("login");
		jMenuItemLogout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		jMenuItemLogout.setActionCommand("logout");
		jMenuItemNouveauFilm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		jMenuItemNouveauFilm.setActionCommand("newFilm");
		//jMenuItemNouveauFilm.setVisible(false);
		BaseVueAppli_jMenuItemListeFilm_actionAdapter al = new BaseVueAppli_jMenuItemListeFilm_actionAdapter(
				this);
		BaseVueAppli_jMenuItemQuitter_actionAdapter quit = new BaseVueAppli_jMenuItemQuitter_actionAdapter(
				this);
		BaseVueAppli_jMenuItemLogin_actionAdapter login = new BaseVueAppli_jMenuItemLogin_actionAdapter(
				this);
		BaseVueAppli_jMenuItemLogout_actionAdapter logout = new BaseVueAppli_jMenuItemLogout_actionAdapter(
				this);
		BaseVueAppli_jMenuItemNouveauFilm_actionAdapter newFilm = new BaseVueAppli_jMenuItemNouveauFilm_actionAdapter(
				this);

		jMenuItemQuitter.addActionListener(quit);
		jMenuItemListeFilm.addActionListener(al);
		jMenuItemLogin.addActionListener(login);
		jMenuItemLogout.addActionListener(logout);
		jMenuItemNouveauFilm.addActionListener(newFilm);
		jMenu1.add(jMenuItemLogin);
		jMenu1.add(jMenuItemLogout);
		jMenu1.add(jMenuItemListeFilm);
		jMenu1.add(jMenuItemNouveauFilm);
		jMenu1.add(jMenuItemQuitter);
		
		jMenuBar1.add(jMenu1);
		this.setJMenuBar(jMenuBar1);

	}

	// affiche
	public void affiche() {
		String methodName = "affiche ";
		// logger.info(methodName + "session.isEtatMenuListe()="+
		// session.isEtatMenuListe());
		// le menu
		jMenuItemLogin.setVisible(true);
		jMenuItemLogout.setVisible(true);
		jMenuItemListeFilm.setVisible(true);
		jMenuItemQuitter.setVisible(true);
		jMenuItemNouveauFilm.setVisible(true);
		jMenu1.add(jMenuItemLogout);
		jMenu1.add(jMenuItemLogin);
		jMenu1.add(jMenuItemListeFilm);
		jMenu1.add(jMenuItemNouveauFilm);
		jMenu1.add(jMenuItemQuitter);
		
		this.setTitle("Dvdtheque");
		// dimension normal
		// this.setExtendedState(JFrame.NORMAL);
		// formulaire degele
		this.setEnabled(true);
		// affichage vue parent
		super.affiche();
	}

	// execute
	protected void executeAction(String action) {
		String methodName = "executeAction ";
		logger.debug(methodName + "action=" + action);
		// fait executer [action] par le controleur
		this.setTitle("Dvdtheque : patientez...");
		// action asynchrone - on gele le formulaire
		this.setEnabled(false);
		// on passe la main a la classe parent
		super.execute(action);
		logger.debug(methodName + "end");
	}

	public void jMenuItemListeFilm_actionPerformed(ActionEvent e) {
		// action [liste]
		this.executeAction("liste");
	}

	void jMenuItemQuitter_actionPerformed(ActionEvent e) {
		// action [liste]
		this.executeAction("quitter");
	}

	void jMenuItemLogin_actionPerformed(ActionEvent e) {
		// action [liste]
		this.executeAction("login");
	}

	void jMenuItemLogout_actionPerformed(ActionEvent e) {
		// action [liste]
		this.executeAction("logout");
	}
	void jMenuItemNouveauFilm_actionPerformed(ActionEvent e) {
		// action [liste]
		this.executeAction("newFilm");
	}
}

class BaseVueAppli_jMenuItemListeFilm_actionAdapter implements
		java.awt.event.ActionListener {
	BaseVueAppli adaptee;

	BaseVueAppli_jMenuItemListeFilm_actionAdapter(BaseVueAppli adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemListeFilm_actionPerformed(e);
	}
}

class BaseVueAppli_jMenuItemQuitter_actionAdapter implements
		java.awt.event.ActionListener {
	BaseVueAppli adaptee;

	BaseVueAppli_jMenuItemQuitter_actionAdapter(BaseVueAppli adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemQuitter_actionPerformed(e);
	}
}

class BaseVueAppli_jMenuItemLogin_actionAdapter implements
		java.awt.event.ActionListener {
	BaseVueAppli adaptee;

	BaseVueAppli_jMenuItemLogin_actionAdapter(BaseVueAppli adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemLogin_actionPerformed(e);
	}
}

class BaseVueAppli_jMenuItemLogout_actionAdapter implements
		java.awt.event.ActionListener {
	BaseVueAppli adaptee;

	BaseVueAppli_jMenuItemLogout_actionAdapter(BaseVueAppli adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemLogout_actionPerformed(e);
	}
}

class BaseVueAppli_jMenuItemNouveauFilm_actionAdapter implements java.awt.event.ActionListener {
	BaseVueAppli adaptee;

	BaseVueAppli_jMenuItemNouveauFilm_actionAdapter(BaseVueAppli adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemNouveauFilm_actionPerformed(e);
	}
}