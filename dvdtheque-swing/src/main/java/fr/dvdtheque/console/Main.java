package fr.dvdtheque.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fr.dvdtheque.console.controleur.IControleur;

public class Main {
	protected final Log logger = LogFactory.getLog(Main.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("main start");
		// variables locales
		IControleur monControleur = null;
		// on fixe le look and feel des vues avant leur création
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// on s'arr�te
			abort("Erreur lors de l'initialisation du look and feel", ex, 4);
		}
		// message de patience
		System.out.println("Application en cours d'initialisation. Patientez...");
		try {
			// on instancie le contrôleur de l'application
			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("swing-applicationContext.xml");
			monControleur = (IControleur) ctx.getBean("controleur");

		} catch (Exception ex) {
			// on s'arrête
			abort("Erreur lors de l'initialisation du controleur DVDTHEQUE-M2VC", ex, 1);
		}
		// exécution application
		System.out.println("Application lancée...");
		try {
			monControleur.run();
		} catch (Exception ex) {
			// on affiche l'erreur et on s'arrête
			abort("Erreur d'exécution", ex, 2);
		}
		// fin normale
		System.out.println("Application terminée...");
		System.exit(0);

	}

	// fin anormale
	private static void abort(String message, Exception ex, int exitCode) throws Exception {
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
