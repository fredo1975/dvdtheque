package fr.dvdtheque.console.action;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.Session;

public class ActionNewFilm extends AbstractBaseAction{
	protected final Log logger = LogFactory.getLog(ActionNewFilm.class);
	@Override
	public String execute() {
		String methodName = "execute ";
		logger.info(methodName + "start");
		// on récupère la session
		getSession().setFilm(null);
		// au départ pas d'erreurs
		ArrayList<String> erreurs = getSession().getErreurs();
		erreurs.clear();
		try {
			return "succes";
		} catch (Exception ex) {
			// on note l'erreur
			erreurs.add(ex.toString());
			return "echec";
		}
	}

}
