package fr.dvdtheque.console.swing;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fr.dvdtheque.console.vue.VueListeFilm;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;

public class TestVueListeFilm {
	protected Logger logger = LoggerFactory.getLogger(TestVueListeFilm.class);
	List<Film> filmList;
	//Specify the look and feel to use.  Valid values:
    //null (use the default), "Metal", "System", "Motif", "GTK+"
    final static String LOOKANDFEEL = null;
    final static boolean MULTICOLORED = false;
    JPanel mainPane;
    VueListeFilm vueListeFilm;
    
	TestVueListeFilm(){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"swing-applicationContext.xml");
		IFilmService filmService = (IFilmService) ctx.getBean("filmService");
        vueListeFilm = new VueListeFilm();
        vueListeFilm.setFilmList(filmService.findAllFilms());
	}
	private JPanel init() throws Exception{
		return vueListeFilm.init();
	}
	private static void createAndShowGUI() {
        //Set the look and feel.
        initLookAndFeel();
 
        //Create and set up the window.
        JFrame frame = new JFrame("VueListeFilm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TestVueListeFilm testVueListeFilm = new TestVueListeFilm();
        testVueListeFilm.vueListeFilm.setOpaque(true);
        try {
			frame.setContentPane(testVueListeFilm.init());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	private static void initLookAndFeel() {
        String lookAndFeel = null;
 
        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (LOOKANDFEEL.equals("GTK+")) { //new in 1.4.2
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }
            try {
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
 
}
