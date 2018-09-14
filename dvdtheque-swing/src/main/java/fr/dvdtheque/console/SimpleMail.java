package fr.dvdtheque.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fr.dvdtheque.console.controleur.IControleur;
import fr.dvdtheque.console.vue.IVue;

public class SimpleMail {
	protected final Log logger = LogFactory.getLog(SimpleMail.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// on fixe le look and feel des vues avant leur création
	    try {
	    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch (Exception ex) {
	    	// on s'arr�te
	    	abort("Erreur lors de l'initialisation du look and feel", ex, 4);
	    }
	    IVue vue = null;
	    IControleur monControleur = null;
	    try {
		      // on instancie le contrôleur de l'application
		    	/*
		      monControleur = (IControleur) (new XmlBeanFactory(new ClassPathResource(
		          "swing-applicationContext.xml"))).getBean("controleur");*/
		    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("swing-applicationContext.xml");
		    	vue = (IVue)ctx.getBean("maVue");
		    	monControleur = (IControleur)ctx.getBean("controleur");
		    }
		    catch (Exception ex) {
		      // on s'arrête
		      abort("Erreur lors de l'initialisation du contrôleur DVDTHEQUE-M2VC", ex, 1);
		    }
		    vue.affiche();
		    //monControleur.run();
	}
	
	private void init() throws Exception {
		
	}
	// fin anormale
	private static void abort(String message, Exception ex, int exitCode)
			throws Exception {
		// on affiche l'erreur
		System.out.println(message);
		System.out.println("---------------------------------------");
		System.out.println(ex.toString());
		System.out.println("---------------------------------------");
		// on laisse l'utilisateur voir le message
		System.out.println("Tapez [enter] pour terminer l'application...");
		BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));
		IN.readLine();
		// on s'arrête
		System.exit(exitCode);
	}
}
