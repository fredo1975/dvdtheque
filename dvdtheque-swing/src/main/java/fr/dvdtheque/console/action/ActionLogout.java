package fr.dvdtheque.console.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionLogout extends AbstractBaseAction {

	protected final Log logger = LogFactory.getLog(ActionLogout.class);
	@Override
	public String execute() {
		String methodName = "execute ";
		logger.info(methodName + "start");
		
		getSession().setUser(null);
		getSession().setEtatMenuLogin(true);
		getSession().setEtatMenuLogout(false);
		getSession().setEtatMenuNouveauFilm(false);
		getSession().setMdp("");
		getSession().setUserName("");
		logger.info(methodName + "logout ... ");
		logger.info(methodName + "end");
		return "succes";
	}
}
