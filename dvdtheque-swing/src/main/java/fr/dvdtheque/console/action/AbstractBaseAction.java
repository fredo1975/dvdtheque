package fr.dvdtheque.console.action;

import fr.dvdtheque.console.Session;

public abstract class AbstractBaseAction implements IAction {
	// la session commune aux actions et vues
	private Session session;
	// getters-setters
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	// méthode execute laissée la charge des classes dérivées
	public abstract String execute();

}
