package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.service.AuthenticatorService;
import fr.fredos.dvdtheque.web.enums.NAVLOGIN;

@ManagedBean(name="loginAction")
@RequestScoped
public class LoginAction implements Serializable{
	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(LoginAction.class);
	private UIComponent loginErrorMsg;
	@ManagedProperty(value="#{authenticatorService}")
	protected AuthenticatorService authenticatorService;
	public void setAuthenticatorService(AuthenticatorService authenticatorService) {
		this.authenticatorService = authenticatorService;
	}
	@ManagedProperty(value="#{user}")
	protected UserBean userBean;
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public UIComponent getLoginErrorMsg() {
		return loginErrorMsg;
	}

	public void setLoginErrorMsg(UIComponent loginErrorMsg) {
		this.loginErrorMsg = loginErrorMsg;
	}
	public void navLogout(ActionEvent event){
		if(userBean.getConnected()) {
			userBean.setConnected(false);
			userBean.setUser(null);
		}
	}
	public String navLogin() {
		if(userBean.getConnected()) {
			userBean.setConnected(false);
			userBean.setUser(null);
			return "default?faces-redirect=true";
		}
		return "login?faces-redirect=true";
	}
	public String authenticate(String login,String mdp) {
		User user = authenticatorService.authenticate(login, mdp);
		if(user!=null) {
			userBean.setConnected(true);
			userBean.setUser(user);
			userBean.setNavLogin(NAVLOGIN.LOGIN.name());
			logger.debug("user "+user.toString()+" connected");
			return "default?faces-redirect=true";
		}
		FacesMessage message = new FacesMessage("Invalide login ou mot de passe");
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(loginErrorMsg.getClientId(context), message);
		return "";
	}
}
