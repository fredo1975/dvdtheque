package fr.dvdtheque.console.table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.dao.model.object.Personne;

public class RealisateurEntry {
	protected final Log logger = LogFactory.getLog(RealisateurEntry.class);
	private Personne realisateur;
	public Personne getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(Personne realisateur) {
		this.realisateur = realisateur;
	}
	public RealisateurEntry() {
		super();
		
	}
	public RealisateurEntry(Personne realisateur) {
		super();
		this.realisateur = realisateur;
	}
	@Override
	public String toString() {
		return realisateur.getPrenom()+" "+realisateur.getNom();
	}
	
	@Override
	public boolean equals(Object obj) {
		String methodName = "equals ";
		if ( obj instanceof RealisateurEntry ) {
			RealisateurEntry realisateurEntry = (RealisateurEntry)obj;
            return (realisateurEntry.getRealisateur().getId() == this.realisateur.getId());
        }
        return false;
	}
	
}
