package fr.fredos.dvdtheque.swing.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.dvdtheque.console.image.utils.ImageUtils;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.swing.service.FilmRestService;

public class FilmTableModel extends AbstractTableModel {
	protected final Log logger = LogFactory.getLog(FilmTableModel.class);
	private static final long serialVersionUID = 1L;
	// les colonnes
	private String[] columnNames = { "", "" };
	
	// les donnees
	private Object[][] data;
	private Map<Integer,Film> filmMap;
	@Autowired
	private FilmRestService filmRestService;

	public void buildFilmList()
			throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		List<Film> filmList = filmRestService.findAllFilms();
		this.filmMap = new HashMap<>(filmList.size());
		data = new Object[filmList.size()][getColumnCount()];
		// on parcourt la liste des articles
		int i = 0;
		for (Film film : filmList) {
			// Icon ii = new ImageIcon(new URL(film.getPosterPath()));
			data[i][0] = ImageUtils.getResizedIcon(film);
			data[i][1] = film.isRipped();
			this.filmMap.put(new Integer(i), film);
			i++;
		}
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return filmMap.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	public Object getFilmAt(int row) {
		return this.filmMap.get(row);
	}
	public boolean getRipped(int row, int col) {
		return (boolean) data[row][col];
	}

	@Override
	public String getColumnName(int col) {
		return "";
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnIndex == 0 ? Icon.class : String.class;
	}
}
