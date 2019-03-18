package fr.dvdtheque.console.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.swing.Session;

public class ActionListe extends AbstractBaseAction {
	protected final Log logger = LogFactory.getLog(ActionListe.class);
	
	@Override
	public String execute() {
		String methodName = "execute ";
		logger.info(methodName + "start");
		// on récupère la session
		Session session = getSession();
		// au d�part pas d'erreurs
		ArrayList<String> erreurs = session.getErreurs();
		erreurs.clear();
		try {
			List<Film> lFilm = session.getFilmService().findAllFilms();
			logger.info("lFilm.size()="+lFilm.size());
			// on demande la liste des articles a la couche métier
			session.setFilmList(lFilm);
			// pas d'erreurs
			return "succes";
		} catch (Exception ex) {
			// on note l'erreur
			erreurs.add(ex.toString());
			return "echec";
		}
	}

}
