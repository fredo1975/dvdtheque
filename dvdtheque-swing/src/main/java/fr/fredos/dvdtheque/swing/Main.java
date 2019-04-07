package fr.fredos.dvdtheque.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.dvdtheque.console.factory.ImageFactory;
import fr.fredos.dvdtheque.swing.model.FilmTableModel;
import fr.fredos.dvdtheque.swing.views.FilmListView;
import fr.fredos.dvdtheque.swing.views.MenuBarPresenter;
import fr.fredos.dvdtheque.swing.views.MenuBarView;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque.swing")
public class Main extends JFrame {
	private static final long serialVersionUID = 1418739757443185022L;
	protected final Log logger = LogFactory.getLog(Main.class);
	private String IMAGE_PATH = "/img/header.JPG";

	private static ConfigurableApplicationContext ctx;

	private void initUI() throws Exception {
		setTitle("Dvdtheque");
		UIManager.setLookAndFeel(new MetalLookAndFeel());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new Double(screenSize.getWidth()).intValue(), new Double(screenSize.getHeight()).intValue());
		setLocationRelativeTo(null);
		// pack();
		
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		this.add(mainPanel);
		buildComponents(mainPanel);
		buildViewsAndPresenters(mainPanel);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void buildViewsAndPresenters(final JPanel mainPanel) throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		final MenuBarView menuBarView = new MenuBarView(this);
		new MenuBarPresenter(menuBarView);
		final FilmListView filmListView = new FilmListView(mainPanel);
		FilmTableModel filmTableModel = (FilmTableModel) ctx.getBean("filmTableModel");
		filmTableModel.buildFilmFilmList();
		//new FilmListPresenter(filmListView);
	}

	private void buildComponents(final JPanel mainPanel) throws Exception {
		initHeader(mainPanel);
	}

	protected void initHeader(JPanel mainPanel) {
		JPanel pan = new JPanel() {
			// Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(pref.width, pref.height);
			}
		};
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		pan.add(new ImageFactory((JPanel) this.getContentPane(), IMAGE_PATH).getHeaderPan());
		mainPanel.add(pan);
	}

	private void createLayout(JComponent... arg) {
		Container pane = getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);
		gl.setAutoCreateContainerGaps(true);
		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(arg[0]));
	}

	public static void main(String[] args) {
		ctx = new SpringApplicationBuilder(Main.class).headless(false).run(args);
		EventQueue.invokeLater(() -> {
			Main ex = ctx.getBean(Main.class);
			ex.setVisible(true);
			try {
				ex.initUI();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}