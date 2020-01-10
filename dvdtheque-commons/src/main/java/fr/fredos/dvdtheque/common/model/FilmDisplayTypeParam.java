package fr.fredos.dvdtheque.common.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.common.enums.FilmDisplayType;

public class FilmDisplayTypeParam {
	protected Logger logger = LoggerFactory.getLogger(FilmDisplayTypeParam.class);
	private final FilmDisplayType filmDisplayType;
	private final int limitFilmSize;
	public FilmDisplayTypeParam(FilmDisplayType filmDisplayType, int limitFilmSize) {
		super();
		this.filmDisplayType = filmDisplayType;
		this.limitFilmSize = limitFilmSize;
	}
	public FilmDisplayType getFilmDisplayType() {
		return filmDisplayType;
	}
	public int getLimitFilmSize() {
		return limitFilmSize;
	}
	@Override
	public String toString() {
		return "FilmDisplayTypeParam [filmDisplayType=" + filmDisplayType + ", limitFilmSize=" + limitFilmSize + "]";
	}
	
}
