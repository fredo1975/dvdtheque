package fr.dvdtheque.console.action;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.Session;
import fr.fredos.dvdtheque.dao.model.object.Film;

public class ActionUpdateFilm extends AbstractBaseAction {
	protected final Log logger = LogFactory.getLog(ActionUpdateFilm.class);
	@Override
	public String execute() {
		String methodName = "execute ";
		logger.info(methodName + "start");
		// on récupère la session
		Session session = getSession();
		// au départ pas d'erreurs
		ArrayList<String> erreurs = session.getErreurs();
		erreurs.clear();
		Film film = null;
		try {
			film = getSession().getFilm();
			logger.info(methodName + "film=" + film.toString());
			//getSession().getFilmService().saveFilm(film);
			// pas d'erreurs
			return "succes";
		} catch (Exception ex) {
			// on note l'erreur
			erreurs.add(ex.toString());
			session.setErreurs(erreurs);
			return "echec";
		}
	}

}
