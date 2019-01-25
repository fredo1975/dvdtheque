package fr.dvdtheque.console.controleur;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.Barriere;
import fr.dvdtheque.console.Session;
import fr.dvdtheque.console.action.InfosAction;
import fr.dvdtheque.console.vue.IVue;

public class BaseControleur implements IControleur {
	protected final Log logger = LogFactory.getLog(BaseControleur.class);
	
	// champs prives
	private Map<String,InfosAction> actions; // les actions e contreler
	private String firstActionName; // le nom de la 1ere action
	private String lastActionName; // le nom de la derniere action
	private Barriere synchro; // l'outil de synchronisation contreleur - vues
	private Session session;
	
	public BaseControleur() {
		super();
		String methodName = "BaseControleur ";
		logger.debug(methodName + "start");
		logger.debug(methodName + "end");
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Map<String,InfosAction> getActions() {
		return actions;
	}
	public void setActions(Map<String,InfosAction> actions) {
		this.actions = actions;
	}
	public String getFirstActionName() {
		return firstActionName;
	}
	public void setFirstActionName(String firstActionName) {
		this.firstActionName = firstActionName;
	}
	public String getLastActionName() {
		return lastActionName;
	}
	public void setLastActionName(String lastActionName) {
		this.lastActionName = lastActionName;
	}
	public Barriere getSynchro() {
		return synchro;
	}
	public void setSynchro(Barriere synchro) {
		this.synchro = synchro;
	}
	// le moteur d'execution des actions
	public void run() {
		String methodName = "run ";
		logger.debug(methodName + "start");
		// variables locales
		InfosAction configAction = null;
		String actionName = null;
		String etat = null;
		IVue vue = null;
		IVue vuePrecedente = null;
		logger.debug(methodName + "lastActionName="+lastActionName);
		logger.debug(methodName + "firstActionName="+firstActionName);
		// boucle d'execution des actions
		actionName = firstActionName;
		if(actionName.equals(lastActionName)){
			System.exit(0);
		}
		while (!actionName.equals(lastActionName)) {
			logger.debug(methodName + " **** actionName="+actionName);
			
			// on recupere la config de l'action
			configAction = actions.get(actionName);
			if (configAction == null) {
				// erreur de config - on s'arrete
				throw new RuntimeException("L'action [" + actionName
						+ "] n'a pas ete configuree correctement");
			}
			logger.debug(methodName + " **** configAction.getAction()="+configAction.getAction());
			// execution de l'action s'il y en a une
			if (configAction.getAction() != null) {
				// execution de l'action
				etat = configAction.getAction().execute();
				logger.debug(methodName + " **** etat="+etat);
				
				// on recupere la vue associee e l'etat
				vue = (IVue) configAction.getEtats().get(etat);
				// si vue == null, erreur de config
				if (vue == null) {
					throw new RuntimeException("L'etat [" + etat
							+ "] de l'action [" + actionName
							+ "] n'a pas ete configure correctement");
				}else{
					logger.debug(methodName + "vue="+vue.getNom());
				}
			} else {
				logger.debug(methodName + " *** pas d'action - directement la vue ***");
				// pas d'action - directement la vue
				etat = "";
				vue = configAction.getVue();
			}
			
			// on cache la vue precedente si elle est differente de celle qui va
			// etre
			// affichee
			if (vue != vuePrecedente && vuePrecedente != null) {
				vuePrecedente.cache();
			}
			// on initialise la vue e afficher
			initVue(actionName, etat, vue.getNom());
			logger.debug(methodName + "on affiche la vue en se synchronisant avec elle actionName="+actionName+" etat="+etat+" vue.getNom()="+vue.getNom());
			
			// on affiche la vue en se synchronisant avec elle
			synchro.reset();
			vue.affiche();
			synchro.waitOne();
			// action suivante
			actionName = vue.getAction();
			vuePrecedente = vue;
		}
		// on cache la derniere vue affichee
		if (vue != null) {
			logger.debug(methodName + "vue cachee ");
			vue.cache();
		}
		// c'est fini
		logger.debug(methodName + "end");
	}
	public void initVue(String action, String etat, String vue) {
		// action deleguee aux classes derivees
		
	}

}
