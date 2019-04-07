package fr.fredos.dvdtheque.swing.model;

import java.io.IOException;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.tmdb.service.FilmRestService;

public class FilmTableModel extends AbstractTableModel {
	protected final Log logger = LogFactory.getLog(FilmTableModel.class);
	private static final long serialVersionUID = 1L;
	// les colonnes
	private String[] columnNames = {"Poster","Ripped"};
	// les donnees
	private Object[][] data;
	private List<Film> filmList;
	@Autowired
	private FilmRestService filmRestService;
	
	public void buildFilmList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		//this.filmList = filmRestService.findAllFilms();
		List<Film> filmAllListResult = filmRestService.findAllFilms();
		data = new Object[filmAllListResult.size()][2];
		// on parcourt la liste des articles
		int i=0;
		for(Film film : filmAllListResult) {
			data[i][0] = film.getPosterPath();
			data[i][1] = film.isRipped();
			i++;
		}
	}
	public int getColumnCount() {
		return columnNames.length;
	}
	public int getRowCount() {
		return filmList.size();
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
