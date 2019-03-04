package fr.fredos.dvdtheque.batch.film.writer;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;

public class DbFilmWriter implements ItemWriter<Film> {
	protected Logger logger = LoggerFactory.getLogger(DbFilmWriter.class);
	@Autowired
	protected IFilmService filmService;
	
	@Override
	public void write(List<? extends Film> items) throws Exception {
		for(Film film : items){
			filmService.saveNewFilm(film);
		}
	}
}
