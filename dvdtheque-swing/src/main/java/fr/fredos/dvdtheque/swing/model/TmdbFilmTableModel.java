package fr.fredos.dvdtheque.swing.model;

import java.net.MalformedURLException;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.collections.CollectionUtils;

import fr.dvdtheque.console.image.utils.ImageUtils;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class TmdbFilmTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	// les colonnes
	private String[] columnNames = { "Poster", "Titre", "Titre Original", "Réalisateur", "Acteurs", "Année", "" };
	// les donnees
	private Object[][] data;
	private Set<Film> filmSet;
	
	public void populateFilmSet(final Set<Film> films) throws MalformedURLException {
		this.filmSet = films;
		data = new Object[filmSet.size()][getColumnCount()];
		// on parcourt la liste des articles
		int i = 0;
		for (Film film : filmSet) {
			// Icon ii = new ImageIcon(new URL(film.getPosterPath()));
			data[i][0] = ImageUtils.getResizedIcon(film);
			data[i][1] = film.getTitre();
			data[i][2] = film.getTitreO();
			data[i][3] = buildPersonneLabel(film.getRealisateurs());
			data[i][4] = buildPersonneLabel(film.getActeurs());
			data[i][5] = film.getAnnee();
			i++;
		}
	}
	public void clearFilmSet() {
		if(CollectionUtils.isNotEmpty(this.filmSet)) {
			this.filmSet.clear();
		}
		//data = new Object[filmSet.size()][0];
	}
	
	private String buildPersonneLabel(final Set<Personne> personnes) {
		StringBuilder sb = new StringBuilder("<html><body>");
		for(Personne personne : personnes) {
			sb.append("<p>"+personne.getNom()+"</p>");
		}
		sb.append("</body></html>");
		return sb.toString();
	}
	@Override
	public int getRowCount() {
		if(CollectionUtils.isNotEmpty(this.filmSet)) {
			return filmSet.size();
		}
		return 0;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnIndex == 0 ? Icon.class : String.class;
	}
}
