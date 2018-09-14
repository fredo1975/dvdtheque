	package fr.dvdtheque.console.table;

import java.util.Iterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import fr.fredos.dvdtheque.dao.model.object.Personne;

public class ActeurListModel implements ListModel<Personne> {
	private Personne[] data;
	java.util.List<Personne> acteurs;
	public ActeurListModel(java.util.List<Personne> acteurs){
		this.acteurs=acteurs;
		data = new Personne[acteurs.size()];
		// on parcourt la liste des Acteurs
		int i=0;
		for(Iterator<Personne> it = acteurs.iterator();it.hasNext();){
			Personne acteur = it.next();
			data[i++] = acteur;
		}
	}
	public void addListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub

	}

	public Personne getElementAt(int arg0) {
		return data[arg0];
	}

	public int getSize() {
		return acteurs.size();
	}

	public void removeListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub

	}

}
