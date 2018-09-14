package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.service.FilmService;

@ManagedBean
@ViewScoped
public class FilmListBean implements Serializable{
	protected Logger logger = LoggerFactory.getLogger(FilmListBean.class);
	private static final long serialVersionUID = 1L;

	private List<FilmDto> filmList;
	private Integer nbFilms;
	@PostConstruct
	public void init() {
		filmList = filmService.getAllFilmDtos();
		nbFilms = filmList.size();
	}
	@ManagedProperty(value="#{filmService}")
	protected FilmService filmService;
	public void setFilmService(FilmService filmService) {
		this.filmService = filmService;
	}
	@ManagedProperty(value="#{personneBean}")
	protected PersonneBean personneBean;
	public void setPersonneBean(PersonneBean personneBean) {
		this.personneBean = personneBean;
	}
	public List<FilmDto> getFilmList() {
		return filmList;
	}
	public void setFilmList(List<FilmDto> filmList) {
		nbFilms = filmList.size();
		this.filmList = filmList;
	}
	public void removeFilm(FilmDto film) {
		logger.debug("film="+film.toString());
		filmService.removeFilm(film);
		filmList.remove(film);
	}
	public Integer getNbFilms() {
		return nbFilms;
	}
	public void setNbFilms(Integer nbFilms) {
		this.nbFilms = nbFilms;
	}
	
}
