package fr.fredos.dvdtheque.swing.model;

import javax.swing.table.AbstractTableModel;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import fr.fredos.dvdtheque.dao.model.object.Film;

public class FilmTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	// les colonnes
	private String[] columnNames = {"Poster","Ripped"};
	// les donnees
	private Object[][] data;
	private java.util.List<Film> films;
	private final RestTemplate restTemplate;
	public FilmTableModel(RestTemplateBuilder restTemplateBuilder) {
		super();
		/*
		this.films = films;
		// on dimensionne le tableau des donnees
		data = new Object[films.size()][2];
		// on parcourt la liste des articles
		Film film = null;
		for (int i = 0; i < films.size(); i++) {
			film = (Film) films.get(i);
			data[i][0] = film.getPosterPath();
			data[i][1] = film.isRipped();
		}*/
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
