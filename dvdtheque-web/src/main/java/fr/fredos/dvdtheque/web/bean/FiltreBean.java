package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;
import java.util.Comparator;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.common.dto.FilmFilterCriteriaDto;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.service.FilmService;

@ManagedBean(name = "filtreBean")
@ViewScoped
public class FiltreBean implements Serializable {
	protected Logger logger = LoggerFactory.getLogger(FiltreBean.class);
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{filmListBean}")
	protected FilmListBean filmListBean;
	private String sort="sort_real";
	public void setFilmListBean(FilmListBean filmListBean) {
		this.filmListBean = filmListBean;
	}
	@ManagedProperty(value = "#{filmService}")
	protected FilmService filmService;
	public void setFilmService(FilmService filmService) {
		this.filmService = filmService;
	}
	private String titre;
	private PersonneDto selectedRealisateur;
	private Integer annee;
	private PersonneDto selectedActeur;
	private Boolean selectedRipped;
	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public PersonneDto getSelectedRealisateur() {
		return selectedRealisateur;
	}

	public void setSelectedRealisateur(PersonneDto selectedRealisateur) {
		this.selectedRealisateur = selectedRealisateur;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public PersonneDto getSelectedActeur() {
		return selectedActeur;
	}

	public void setSelectedActeur(PersonneDto selectedActeur) {
		this.selectedActeur = selectedActeur;
	}

	public Boolean getSelectedRipped() {
		return selectedRipped;
	}

	public void setSelectedRipped(Boolean selectedRipped) {
		this.selectedRipped = selectedRipped;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}

	public void clean(ActionEvent actionEvent) {
		resetFields();
		selectedActeur = null;
		filmListBean.setFilmList(filmService.getAllFilmDtos());
	}

	private void resetFields() {
		titre = null;
		selectedRealisateur = null;
		annee = null;
		selectedRipped = null;
	}
	public void selectActeur(ValueChangeEvent valueChangeEvent) {
		resetFields();
	}
	public void launch(ActionEvent actionEvent) {
		FilmFilterCriteriaDto filmFilterCriteriaDto = new FilmFilterCriteriaDto(titre, annee,
				selectedRealisateur != null && selectedRealisateur.getId() != null ? selectedRealisateur.getId() : null,
				selectedActeur != null && selectedActeur.getId() != null ? selectedActeur.getId() : null,
				selectedRipped);
		filmListBean.setFilmList(filmService.findAllFilmsByCriteria(filmFilterCriteriaDto));
	}
	public void sortList(ValueChangeEvent valueChangeEvent) {
		String sort = (String) valueChangeEvent.getNewValue();
		if(sort.equals("sort_titre")) {
			filmListBean.getFilmList().sort(Comparator.comparing(FilmDto::getTitre));
		}else {
			filmListBean.getFilmList().sort(Comparator.comparing(FilmDto::getPrintRealisateur).thenComparing(FilmDto::getTitre));
		}
		
	}
}
