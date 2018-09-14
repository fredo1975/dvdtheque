package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.service.AuthenticatorService;
import fr.fredos.dvdtheque.web.enums.NAVLOGIN;
@ManagedBean(name="user")
@SessionScoped
public class UserBean implements Serializable{
	protected Logger logger = LoggerFactory.getLogger(UserBean.class);
	private static final long serialVersionUID = 1L;
	private User user = null;
	private boolean connected;
	private String navLogin=NAVLOGIN.NULL.name();
	@ManagedProperty(value="#{authenticatorService}")
	protected AuthenticatorService authenticatorService;
	public void setAuthenticatorService(AuthenticatorService authenticatorService) {
		this.authenticatorService = authenticatorService;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean getConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public String getNavLogin() {
		return navLogin;
	}
	public void setNavLogin(String navLogin) {
		this.navLogin = navLogin;
	}
	
	
}
