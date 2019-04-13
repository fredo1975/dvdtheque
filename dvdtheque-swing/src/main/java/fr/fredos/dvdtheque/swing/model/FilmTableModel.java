package fr.fredos.dvdtheque.swing.model;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.swing.service.FilmRestService;
import fr.fredos.dvdtheque.swing.views.AbstractViewListenerHolder;

public class FilmTableModel extends AbstractTableModel {
	protected final Log logger = LogFactory.getLog(FilmTableModel.class);
	private static final long serialVersionUID = 1L;
	// les colonnes
	private String[] columnNames = {"",""};
	// les donnees
	private Object[][] data;
	private List<Film> filmList;
	@Autowired
	private FilmRestService filmRestService;
	
	public void buildFilmList() throws JsonParseException, JsonMappingException, RestClientException, IllegalStateException, IOException {
		this.filmList = filmRestService.findAllFilms();
		data = new Object[filmList.size()][2];
		// on parcourt la liste des articles
		int i=0;
		for(Film film : filmList) {
			//Icon ii = new ImageIcon(new URL(film.getPosterPath()));
			data[i][0] = getResizedIcon(film);
			data[i][1] = film.isRipped();
			i++;
		}
	}
	private Icon getResizedIcon(final Film film) throws MalformedURLException {
		ImageIcon imageIcon = new ImageIcon(new URL(film.getPosterPath()));
		Image image = imageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(AbstractViewListenerHolder.IMAGE_WIDTH_SIZE, AbstractViewListenerHolder.IMAGE_HEIGHT_SIZE,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		return new ImageIcon(newimg);  // transform it back
	}
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	@Override
	public int getRowCount() {
		return filmList.size();
	}
	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	public boolean getRipped(int row, int col) {
		return (boolean)data[row][col];
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
		return columnIndex==0? Icon.class : String.class;
	}
	
}
