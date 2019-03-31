package fr.fredos.dvdtheque.swing.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.swing.Main;
import fr.fredos.dvdtheque.swing.service.FilmRestService;

@Component
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
	
	public FilmTableModel() {
		super();
		/*
		this.films = filmRestService.findAllFilms();
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
	
	public void buildFilmFilmList() {
		//this.filmList = filmRestService.findAllFilms();
		List<LinkedHashMap<String, Object>> filmAllListResult = filmRestService.findAllFilms();
		data = new Object[filmAllListResult.size()][2];
		int i=0;
		for(LinkedHashMap<String, Object> linked : filmAllListResult) {
			for(Map.Entry<String, Object> entry : linked.entrySet()) {
				String key = entry.getKey();
				Object val = entry.getValue();
				logger.info("key="+key+" val="+val);
				/*data[i][0] = film.getPosterPath();
				data[i][1] = film.isRipped();
				*/
				
			}
			String posterPath = (String) linked.get("posterPath");
			boolean ripped = (boolean) linked.get("ripped");
			logger.info("posterPath="+posterPath+" ripped="+ripped);
			data[i][0] = posterPath;
			data[i][1] = ripped;
			i++;
		}
		
		
		/*
		// on parcourt la liste des articles
		
		for(LinkedHashMap<String,Film> LinkedHashMapFilm : this.filmList) {
			data[i][0] = film.getPosterPath();
			data[i][1] = film.isRipped();
			i++;
		}*/
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
