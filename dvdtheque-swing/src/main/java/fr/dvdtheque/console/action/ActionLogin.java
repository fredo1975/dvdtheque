package fr.dvdtheque.console.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionLogin extends AbstractBaseAction {
	protected final Log logger = LogFactory.getLog(ActionLogin.class);
	@Override
	public String execute() {
		String methodName = "execute ";
		logger.info(methodName + "start");
		
		logger.info(methodName + "end");
		return "succes";
	}

}
