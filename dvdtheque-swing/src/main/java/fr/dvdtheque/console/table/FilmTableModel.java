package fr.dvdtheque.console.table;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.collections.CollectionUtils;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class FilmTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	// les colonnes
	private String[] columnNames = {"Titre", "Realisateur", "Année","Détail","Ripped"};
	// les donnees
	private Object[][] data;
	private java.util.List<Film> films;
	public FilmTableModel(java.util.List<Film> films) {
		super();
		// on mémorise la référence de la liste des articles
		this.films = films;
		// on dimensionne le tableau des donnees
		data = new Object[films.size()][5];
		// on parcourt la liste des articles
		Film film = null;
		for (int i = 0; i < films.size(); i++) {
			film = (Film) films.get(i);
			data[i][0] = film.getTitre();
			Set<Personne> realisateurs = film.getRealisateurs();
			if(CollectionUtils.isNotEmpty(realisateurs) && realisateurs.size()==1){
				Personne realisateur = film.getRealisateurs().iterator().next();
				data[i][1] = realisateur.getPrenom()+" "+realisateur.getNom();
			}
			data[i][2] = film.getAnnee();
			Set<Personne> acteurs = film.getActeurs();
			if(CollectionUtils.isNotEmpty(acteurs)){
				//data[i][3] = film.getActeurs();
				StringBuilder sb = new StringBuilder("");
				for(Personne p : film.getActeurs()){
					sb.append(p.getPrenom()).append(" ").append(p.getNom());
					sb.append(",");
				}
				data[i][3] = sb.toString();
			}
			data[i][4] = film.isRipped();
		}
		
	}
	public int getColumnCount() {
		return columnNames.length;
	}
	public int getRowCount() {
		return films.size();
	}
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	public boolean getRipped(int row, int col) {
		return (boolean)data[row][col];
	}
	public String getColumnName(int col) {
        return columnNames[col].toString();
    }
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
}
