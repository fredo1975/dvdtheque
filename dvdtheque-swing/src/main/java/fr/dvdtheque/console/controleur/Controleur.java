package fr.dvdtheque.console.controleur;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.Session;

public class Controleur extends BaseControleur {
	protected final Log logger = LogFactory.getLog(Controleur.class);
	
	private Session session;
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	public Controleur() {
		super();
	}
	public void initVue(String action, String etat, String vue) {
		String methodName = "initVue ";
		logger.debug(methodName + "start vue="+vue+" etat="+etat+" action="+action);
		// on fixe les options de menu de la vue � afficher
		// selon l'action [action] en cours
		// l'�tat [�tat] r�sultant de cette action
		// la vue [vue] qui va �tre affich�e
		// l'option [quitter] est toujours active
		session.setEtatMenuQuitter(true);
		// l'option [liste]
		session.setEtatMenuListe(true);
		//logger.info(methodName + "session="+session.isEtatMenuListe());
		/*
		// l'option [voir le panier]
		session.setEtatMenuVoirPanier("liste".equals(vue)
				|| "validerpanier".equals(action));
		// l'option [valider le panier]
		session.setEtatMenuValiderPanier("panier".equals(vue));*/
		// le message de la vue [liste]
		logger.debug(methodName + "vue="+vue+" action="+action);
		if ("liste".equals(vue)) {
			if ("validerpanier".equals(action)) {
				session.setMessage("Validation réussie !");
			} else {
				session.setMessage("");
			}
		}
		logger.debug(methodName + "end");
	}
}
