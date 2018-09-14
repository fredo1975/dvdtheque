package fr.dvdtheque.console.action;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.Session;

public class ActionFilm extends AbstractBaseAction {
protected final Log logger = LogFactory.getLog(ActionFilm.class);
	
	@Override
	public String execute() {
		String methodName = "execute ";
		logger.info(methodName + "start");
		// on récupère la session
		Session session = getSession();
		// au départ pas d'erreurs
		ArrayList<String> erreurs = session.getErreurs();
		erreurs.clear();
		try {
			// pas d'erreurs
			return "succes";
		} catch (Exception ex) {
			// on note l'erreur
			erreurs.add(ex.toString());
			return "echec";
		}
	}

}
