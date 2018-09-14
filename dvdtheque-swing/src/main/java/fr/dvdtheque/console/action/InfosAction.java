package fr.dvdtheque.console.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.vue.IVue;

public class InfosAction {
	protected final Log logger = LogFactory.getLog(InfosAction.class);
	
	private IAction action; // l'action a éxécuter
	private IVue vue; // la vue a afficher
	private Map<String,IVue> etats; // le dictionnaire des états de l'action a exécuter

	public IAction getAction() {
		return action;
	}
	public void setAction(IAction action) {
		this.action = action;
	}
	public Map<String,IVue> getEtats() {
		return etats;
	}
	public void setEtats(Map<String,IVue> etats) {
		this.etats = etats;
	}
	public IVue getVue() {
		return vue;
	}
	public void setVue(IVue vue) {
		this.vue = vue;
	}
}
