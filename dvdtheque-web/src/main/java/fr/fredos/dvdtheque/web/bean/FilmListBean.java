package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dto.ActeurDto;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.service.FilmService;

@ManagedBean
@ViewScoped
public class FilmListBean implements Serializable{
	protected Logger logger = LoggerFactory.getLogger(FilmListBean.class);
	private static final long serialVersionUID = 1L;
	private List<PersonneDto> realSelectItemList;
	private List<PersonneDto> actSelectItemList;
	private DualListModel<PersonneDto> acteursListModel;
	private List<FilmDto> filmList;
	private Integer nbFilms;
	@PostConstruct
	public void init() {
		filmList = filmService.getAllFilmDtos();
		List<PersonneDto> realSource = new ArrayList<>();
        List<PersonneDto> actSource = new ArrayList<>();
        List<PersonneDto> actTarget = new ArrayList<>();
        realSelectItemList = new ArrayList<>();
        actSelectItemList = new ArrayList<>();
        Set<PersonneDto> realSet = new HashSet<>();
        Set<PersonneDto> actSet = new HashSet<>();
        Set<PersonneDto> actSourceSet = new HashSet<>();
        for(FilmDto film : filmService.getAllFilmDtos()) {
        	realSet.add(film.getPersonnesFilm().getRealisateur().getPersonne());
        	if(CollectionUtils.isNotEmpty(film.getPersonnesFilm().getActeurs())) {
        		for(ActeurDto actDto : film.getPersonnesFilm().getActeurs()) {
            		actSet.add(actDto.getPersonne());
            		actSourceSet.add(actDto.getPersonne());
            	}
        	}
        }
        realSource.addAll(realSet);
        realSelectItemList.addAll(realSet);
        actSelectItemList.addAll(actSet);
        actSource.addAll(actSourceSet);
        Collections.sort(realSelectItemList);
        Collections.sort(actSelectItemList);
        acteursListModel = new DualListModel<>(actSource, actTarget);
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
	public List<PersonneDto> getRealSelectItemList() {
		return realSelectItemList;
	}
	public void setRealSelectItemList(List<PersonneDto> realSelectItemList) {
		this.realSelectItemList = realSelectItemList;
	}
	public List<PersonneDto> getActSelectItemList() {
		return actSelectItemList;
	}
	public void setActSelectItemList(List<PersonneDto> actSelectItemList) {
		this.actSelectItemList = actSelectItemList;
	}
	public DualListModel<PersonneDto> getActeursListModel() {
		return acteursListModel;
	}
	public void setActeursListModel(DualListModel<PersonneDto> acteursListModel) {
		this.acteursListModel = acteursListModel;
	}
	
}
