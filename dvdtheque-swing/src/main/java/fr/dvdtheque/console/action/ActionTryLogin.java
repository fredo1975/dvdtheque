package fr.dvdtheque.console.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.dao.model.object.User;

public class ActionTryLogin extends AbstractBaseAction{
	protected final Log logger = LogFactory.getLog(ActionTryLogin.class);
	@Override
	public String execute() {
		String methodName = "execute ";
		logger.info(methodName + "start");
		User user = null;
		
		try {
			if(null != getSession().getUserName()
					&& !getSession().getUserName().equals("")
					&& null != getSession().getMdp()
					&& !getSession().getMdp().equals("")){
				
				logger.info(methodName + "getSession().getUserName()="+getSession().getUserName());
				logger.info(methodName + "getSession().getMdp()="+getSession().getMdp());
				
				user = getSession().getAuthenticatorService().authenticate(getSession().getUserName(), getSession().getMdp());
				logger.info(methodName + "user="+user.getFirstName()+" "+user.getLastName());
				
				getSession().setUser(user);
				getSession().setEtatMenuLogin(false);
				getSession().setEtatMenuLogout(true);
				getSession().setEtatMenuNouveauFilm(true);
				
			}else{
				if(null == getSession()){
					logger.info(methodName + "null == getSession()");
					
				}
				if(null == getSession().getUserName()){
					logger.info(methodName + "null == getSession().getUserName()");
					
				}
				return "echec";
			}
			
		} catch (org.springframework.dao.EmptyResultDataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "echec";
		}
		logger.info(methodName + "try login ... ");
		logger.info(methodName + "end");
		return "succes";
	}
}
