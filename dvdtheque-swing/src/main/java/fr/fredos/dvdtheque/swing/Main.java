package fr.fredos.dvdtheque.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import fr.dvdtheque.console.factory.ImageFactory;

@SpringBootApplication(scanBasePackages = {"fr.fredos.dvdtheque.batch",
		"fr.fredos.dvdtheque.service",
		"fr.fredos.dvdtheque.dao",
		"fr.fredos.dvdtheque.tmdb.service",
		"fr.fredos.dvdtheque.swing"})
public class Main extends JFrame {
	private static final long serialVersionUID = 1418739757443185022L;
	protected final Log logger = LogFactory.getLog(Main.class);
	private String IMAGE_PATH = "/img/header.JPG";
	//private JPanel mainPanel;
	private Dimension screenSize;
	static ConfigurableApplicationContext ctx;
	@Autowired
	JPanel headerJPanel;
	@Autowired
	private JPanel contentPane;
	@Autowired
	private JPanel subPanel;
	private void initUI() throws Exception {
		setTitle("Dvdtheque");
		UIManager.setLookAndFeel(new MetalLookAndFeel());
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new Double(this.screenSize.getWidth()).intValue(), new Double(this.screenSize.getHeight()).intValue()-50);
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		this.add(contentPane);
		initHeader();
		contentPane.add(headerJPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		contentPane.add(subPanel, BorderLayout.CENTER);
		this.setVisible(true);
	}

	protected void initHeader() {
		headerJPanel.setLayout(new BoxLayout(headerJPanel, BoxLayout.PAGE_AXIS));
		JPanel pan = new JPanel() {
			private static final long serialVersionUID = 1L;
			// Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(pref.width, pref.height);
			}
		};
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		pan.add(new ImageFactory((JPanel) this.getContentPane(), IMAGE_PATH).getHeaderPan());
		headerJPanel.add(pan);
	}

	public static void main(String[] args) {
		/* Use an appropriate Look and Feel */
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		ctx = new SpringApplicationBuilder(Main.class).headless(false).run(args);
		EventQueue.invokeLater(() -> {
			Main main = ctx.getBean(Main.class);
			main.setVisible(true);
			try {
				main.initUI();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public Dimension getScreenSize() {
		return screenSize;
	}
	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
	}
}