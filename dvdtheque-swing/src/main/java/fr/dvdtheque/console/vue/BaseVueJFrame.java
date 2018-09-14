package fr.dvdtheque.console.vue;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.Barriere;

public class BaseVueJFrame extends JFrame implements IVue, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final Log logger = LogFactory.getLog(BaseVueJFrame.class);
	
	// champs privés
	private String nom; // nom de la vue
	private String action; // nom de l'action que le contrôleur doit exécuter
	private Barriere synchro; // objet de synchronisation controleur - vues
	private Thread threadVue; // objet de synchronisation de la vue

	/**
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return Returns the nom.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom
	 *            The nom to set.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return Returns the synchro.
	 */
	public Barriere getSynchro() {
		return synchro;
	}

	/**
	 * @param synchro
	 *            The synchro to set.
	 */
	public void setSynchro(Barriere synchro) {
		this.synchro = synchro;
	}

	// affiche la vue
	public void affiche() {
		String methodName = "affiche ";
		logger.debug(methodName + "start");
		
		// on s'affiche dans le thread d'affichage
		if (threadVue == null) {
			// on créé le thread d'affichage
			threadVue = new Thread(this);
			threadVue.start();
		} else {
			// on s'affiche
			this.setVisible(true);
			logger.debug(methodName + "on s'affiche");
		}
		logger.debug(methodName + "end");
		
	}

	// cache la vue
	public void cache() {
		// on se cache
		this.setVisible(false);
	}

	// action asynchrone
	public void execute(String action) {
		// on note l'action à exécuter
		this.setAction(action);
		// on passe la main au controleur
		synchro.set();
	}

	// méthode du thread d'affichage
	public void run() {
		// affichage de la vue
		this.setVisible(true);
	}

}
