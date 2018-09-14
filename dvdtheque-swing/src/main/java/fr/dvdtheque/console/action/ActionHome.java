package fr.dvdtheque.console.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionHome extends AbstractBaseAction{
	protected final Log logger = LogFactory.getLog(ActionHome.class);

	@Override
	public String execute() {
		return "succes";
	}
	
}
