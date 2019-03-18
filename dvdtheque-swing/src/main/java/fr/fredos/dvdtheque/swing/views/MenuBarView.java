package fr.fredos.dvdtheque.swing.views;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.swing.view.listener.ViewListener;

public class MenuBarView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(MenuBarView.class);
	private JFrame frame;
	private JMenuBar jMenuBar1 = new JMenuBar();
	private JMenu jMenu1 = new JMenu("Actions");
	private JMenuItem jMenuItemLogin = new JMenuItem("Login");
	private JMenuItem jMenuItemLogout = new JMenuItem("Logout");
	private JMenuItem jMenuItemQuitter = new JMenuItem("Quitter");
	
	public MenuBarView(final JFrame frame) {
		this.frame = frame;
		buildMenu();
    }
	
	private void buildMenu() {
		jMenuItemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jMenuItemQuitter.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		jMenuItemQuitter.setActionCommand("quitter");
		jMenuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jMenuItemLogin.setActionCommand("login");
		jMenuItemLogout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jMenuItemLogout.setActionCommand("logout");
		
		jMenuItemQuitter.addActionListener((event) -> {
            notifyListeners(ViewListener::onQuitMenuChoosed, event);
        });
		
		jMenuItemLogin.setVisible(true);
		jMenuItemLogout.setVisible(true);
		jMenuItemQuitter.setVisible(true);
		
		jMenu1.add(jMenuItemLogin);
		jMenu1.add(jMenuItemLogout);
		jMenu1.add(jMenuItemQuitter);
		jMenuBar1.add(jMenu1);
		this.frame.setJMenuBar(jMenuBar1);
		
	}
}