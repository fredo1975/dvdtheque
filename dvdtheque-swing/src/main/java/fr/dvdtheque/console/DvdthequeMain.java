package fr.dvdtheque.console;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fr.dvdtheque.console.factory.ImageFactory;
import fr.dvdtheque.console.vue.VueListeFilm;
import fr.dvdtheque.console.vue.VueTryLogin;
import fr.fredos.dvdtheque.dao.model.object.Film;

@SuppressWarnings("serial")
public class DvdthequeMain extends JFrame {
	private static DvdthequeMain rootFrame;
	protected Logger logger = LoggerFactory.getLogger(DvdthequeMain.class);
	private String titleBase;
	private Dimension screenSize;
	private Dimension frameSize;
	protected JPanel contentPane;
	private JLabel jLabel1;
	protected JPanel jPanel1 = new JPanel();
	protected JPanel jPanel, panel;
	protected JMenuBar jMenuBar1 = new JMenuBar();
	protected JMenu jMenu1 = new JMenu("Actions");
	protected JMenuItem jMenuItemLogin = new JMenuItem("Login");
	protected JMenuItem jMenuItemLogout = new JMenuItem("Logout");
	protected JMenuItem jMenuItemQuitter = new JMenuItem("Quitter");
	// session de l'application commune aux vues et actions
	private static Session session;
	private VueListeFilm vueListeFilm;
	private String imagePath = "/img/header.JPG";

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 * @throws Exception 
	 */
	public static void createAndShowGUI() throws Exception {

		Date start = new Date();
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"swing-applicationContext.xml");
		session = (Session) ctx.getBean("session");
		rootFrame = new DvdthequeMain("Dvdtheque");

		rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rootFrame.pack();
		rootFrame.setSize(1024, 768);

		rootFrame.buildComponents();

		// centerOnScreen(rootFrame);

		rootFrame.setVisible(true);

		System.out.println("-- Dvdtheque gui launched in "
				+ (new Date().getTime() - start.getTime()) / 1000 + " sec --");
	}

	public static DvdthequeMain getRootFrame() {
		return rootFrame;
	}

	public static void main(String args[]) {
		try {
			/*
			 * la fa�on bourin de supprimer les check de securit� pour ne pas
			 * avoir � donner des droits sur chaque poste client
			 */
			System.setSecurityManager(null);

			// UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
		} catch (Exception ex) {
			//logger.error(ex.getMessage(), ex);
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					DvdthequeMain.createAndShowGUI();
				} catch (Exception ex) {
					//logger.error(ex.getMessage(), ex);
				}
			}
		});
	}

	private DvdthequeMain(String title) {
		super(title);
		this.titleBase = title;
	}

	private void buildComponents() throws Exception {
		vueListeFilm = new VueListeFilm();
		init();
	}

	// Initialiser le composant
	private void init() throws Exception {
		String methodName = "init ";
		logger.info(methodName + "start System.getProperty(user.dir)="
				+ System.getProperty("user.dir"));
		logger.info(methodName + "start System.getProperty(java.class.path)="
				+ System.getProperty("java.class.path"));

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize = this.getSize();
		logger.info(methodName + "screenSize=" + screenSize);
		logger.info(methodName + "frameSize=" + frameSize);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		JPanel initPanel = initHeader();
		jLabel1 = new JLabel("DVDTHEQUE");
		jLabel1.setFont(new Font(jLabel1.getFont().getFamily(), Font.BOLD, 15));
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.PAGE_AXIS));
		bodyPanel.add(jLabel1);
		//bodyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		bodyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		initPanel.add(bodyPanel);
		mainPanel.add(initPanel);
		
		List<Film> filmList = session.getFilmService().findAllFilms();
		
		if(CollectionUtils.isNotEmpty(filmList)){
			vueListeFilm.setFilmList(filmList);
		}else{
			JOptionPane.showMessageDialog(this, "no film in db", "an error occured", JOptionPane.WARNING_MESSAGE);
		}
		try {
			mainPanel.add(vueListeFilm.init());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(this, "an error occured while opening film list", "an error occured", JOptionPane.ERROR_MESSAGE);
		}
		this.getContentPane().add(mainPanel);
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
		jMenuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		jMenuItemLogin.setActionCommand("login");
		jMenuItemLogout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		jMenuItemLogout.setActionCommand("logout");
		BaseVueAppli_jMenuItemQuitter_actionAdapter quit = new BaseVueAppli_jMenuItemQuitter_actionAdapter(
				this);
		BaseVueAppli_jMenuItemLogin_actionAdapter login = new BaseVueAppli_jMenuItemLogin_actionAdapter(
				this);
		BaseVueAppli_jMenuItemLogout_actionAdapter logout = new BaseVueAppli_jMenuItemLogout_actionAdapter(
				this);
		jMenuItemLogin.setVisible(true);
		jMenuItemLogout.setVisible(true);
		jMenuItemQuitter.setVisible(true);
		jMenuItemQuitter.addActionListener(quit);
		jMenuItemLogin.addActionListener(login);
		jMenuItemLogout.addActionListener(logout);
		jMenu1.add(jMenuItemLogin);
		jMenu1.add(jMenuItemLogout);
		jMenu1.add(jMenuItemQuitter);
		jMenuBar1.add(jMenu1);
		this.setJMenuBar(jMenuBar1);
		// dimension normal
		// this.setExtendedState(JFrame.NORMAL);
		// formulaire d�gel�
		this.setEnabled(true);
	}
	public String getImagePath() {
		return imagePath;
	}
	protected JPanel initHeader(){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JPanel pan = new JPanel() {
			// Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(pref.width, pref.height);
			}
		};
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		
		ImageFactory imageFactory = new ImageFactory((JPanel) this.getContentPane(),getImagePath());
		
		pan.add(imageFactory.getHeaderPan());
		panel.add(pan);
		return panel;
	}
	void jMenuItemQuitter_actionPerformed(ActionEvent e) {
		// action [liste]
		// fin normale
	    System.out.println("Application termin�e...");
	    System.exit(0);
	}

	void jMenuItemLogin_actionPerformed(ActionEvent e) {
		VueTryLogin vueLogin = new VueTryLogin();
		
	}

	void jMenuItemLogout_actionPerformed(ActionEvent e) {
		// action [liste]
		//this.ex�cuteAction("logout");
	}

}

class BaseVueAppli_jMenuItemQuitter_actionAdapter implements
		java.awt.event.ActionListener {
	DvdthequeMain adaptee;

	BaseVueAppli_jMenuItemQuitter_actionAdapter(DvdthequeMain adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemQuitter_actionPerformed(e);
	}
}

class BaseVueAppli_jMenuItemLogin_actionAdapter implements
		java.awt.event.ActionListener {
	DvdthequeMain adaptee;

	BaseVueAppli_jMenuItemLogin_actionAdapter(DvdthequeMain adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemLogin_actionPerformed(e);
	}
}

class BaseVueAppli_jMenuItemLogout_actionAdapter implements
		java.awt.event.ActionListener {
	DvdthequeMain adaptee;

	BaseVueAppli_jMenuItemLogout_actionAdapter(DvdthequeMain adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItemLogout_actionPerformed(e);
	}
}
