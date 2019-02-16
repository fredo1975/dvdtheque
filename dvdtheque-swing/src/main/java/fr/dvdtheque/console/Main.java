package fr.dvdtheque.console;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import fr.dvdtheque.console.factory.ImageFactory;
@SpringBootApplication
public class Main extends JFrame{
	private static final long serialVersionUID = 1418739757443185022L;
	protected final Log logger = LogFactory.getLog(Main.class);
	private String imagePath = "/img/header.JPG";
	public Main() throws Exception {
        initUI();
    }

    private void initUI() throws Exception {
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener((ActionEvent event) -> System.exit(0));
        createLayout(quitButton);
        setTitle("Quit button");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        pack();
        buildComponents();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    // Initialiser le composant
 	private void buildComponents() throws Exception {
 		String methodName = "init ";
 		logger.info(methodName + "start System.getProperty(user.dir)="
 				+ System.getProperty("user.dir"));
 		logger.info(methodName + "start System.getProperty(java.class.path)="
 				+ System.getProperty("java.class.path"));
 		
 		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
 		Dimension frameSize = this.getSize();
 		logger.info(methodName + "screenSize=" + screenSize);
 		logger.info(methodName + "frameSize=" + frameSize);

 		JPanel mainPanel = new JPanel();
 		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
 		
 		initHeader(mainPanel);
 		
 		logger.info(methodName + "end");
 	}
 	protected void initHeader(JPanel mainPanel){
		JPanel pan = new JPanel() {
			// Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(pref.width, pref.height);
			}
		};
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		
		ImageFactory imageFactory = new ImageFactory((JPanel) this.getContentPane(), imagePath);
		
		pan.add(imageFactory.getHeaderPan());
		mainPanel.add(pan);
	}
    private void createLayout(JComponent... arg) {
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
        );
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
        );
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Main.class).headless(false).run(args);

        EventQueue.invokeLater(() -> {
        	Main ex = ctx.getBean(Main.class);
            ex.setVisible(true);
        });
    }
}
