package fr.fredos.dvdtheque.swing.views;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import fr.fredos.dvdtheque.swing.Main;
import fr.fredos.dvdtheque.swing.SpinnerDialog;
import fr.fredos.dvdtheque.swing.view.listener.MenuViewListener;

public class MenuBarView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(MenuBarView.class);
	private JMenuBar jMenuBar1 = new JMenuBar();
	private JMenu jMenu1 = new JMenu("Actions");
	private JMenuItem jMenuItemLogin = new JMenuItem("Login");
	private JMenuItem jMenuItemFilmList = new JMenuItem("Liste des films");
	private JMenuItem jMenuItemLogout = new JMenuItem("Logout");
	private JMenuItem jMenuItemQuitter = new JMenuItem("Quitter");
	@Autowired
	private Main main;
	@Autowired
	private SpinnerDialog spinnerDialog;
	@PostConstruct
	protected void init() {
		this.buildMenu();
	}
	private void buildMenu() {
		jMenuItemFilmList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jMenuItemQuitter.setActionCommand("liste");
		jMenuItemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jMenuItemQuitter.setActionCommand("quitter");
		jMenuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jMenuItemLogin.setActionCommand("login");
		jMenuItemLogout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jMenuItemLogout.setActionCommand("logout");
		
		jMenuItemQuitter.addActionListener((event) -> {
            notifyMenuListeners(MenuViewListener::onQuitMenuChoosed, event);
        });
		
		jMenuItemFilmList.addActionListener((event) -> {
			spinnerDialog.setFrame(main);
			SwingWorker<?, ?> worker = new SwingWorker<Void, Integer>() {
				protected Void doInBackground() throws InterruptedException {
					notifyMenuListeners((t, u) -> {
						try {
							t.onFilmListMenuChoosed(u);
						} catch (RestClientException | IllegalStateException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}, event);
					return null;
				}

				protected void process(List<Integer> chunks) {
					
				}

				protected void done() {
					spinnerDialog.dispose();
					//btn.setText("Do Some Long Task");
				}
			};
			worker.execute();
			spinnerDialog.setVisible();
        });
		
		jMenuItemLogin.setVisible(true);
		jMenuItemLogout.setVisible(true);
		jMenuItemQuitter.setVisible(true);
		
		jMenu1.add(jMenuItemLogin);
		jMenu1.add(jMenuItemFilmList);
		jMenu1.add(jMenuItemLogout);
		jMenu1.add(jMenuItemQuitter);
		jMenuBar1.add(jMenu1);
		main.setJMenuBar(jMenuBar1);
	}
}