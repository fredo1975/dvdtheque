package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dto.ActeurDto;
import fr.fredos.dvdtheque.dto.DvdDto;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.dto.PersonnesFilm;
import fr.fredos.dvdtheque.dto.RealisateurDto;
import fr.fredos.dvdtheque.enums.ZoneDvd;
import fr.fredos.dvdtheque.service.FilmService;
import fr.fredos.dvdtheque.service.PersonneService;

@ManagedBean(name="filmBean")
@SessionScoped
public class FilmBean implements Serializable{
	protected Logger logger = LoggerFactory.getLogger(FilmBean.class);
	private static final long serialVersionUID = 1L;
	private FilmDto filmDto;
	private PersonneDto selectedRealisateur;
	private ZoneDvd selectedZone;
	private List<ZoneDvd> zoneDvdList;
	private List<Integer> anneeList;
	private List<Integer> anneeDvdList;
	private Integer selectedAnnee;
	private Integer selectedAnneeDvd;
	private boolean selectedRipped;
	private java.lang.Integer id;
	@ManagedProperty(value="#{personneListBean}")
	protected PersonneListBean personneListBean;
	public void setPersonneListBean(PersonneListBean personneListBean) {
		this.personneListBean = personneListBean;
	}
	@ManagedProperty(value="#{filmService}")
	protected FilmService filmService;
	public void setFilmService(FilmService filmService) {
		this.filmService = filmService;
	}
	@ManagedProperty(value="#{personneService}")
	protected PersonneService personneService;
	public void setPersonneService(PersonneService personneService) {
		this.personneService = personneService;
	}
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	
	public FilmDto getFilmDto() {
		return filmDto;
	}
	public void setFilmDto(FilmDto filmDto) {
		this.filmDto = filmDto;
	}
	private void fillAnneeList() {
		Calendar cal = Calendar.getInstance();
		Integer currentAnnee = cal.get(Calendar.YEAR);
		Integer annee0 = 1930;
		anneeList = new ArrayList<>(currentAnnee-annee0);
		anneeDvdList = new ArrayList<>(currentAnnee-annee0);
		for(int i = annee0;i<currentAnnee+1;i++) {
			anneeList.add(new Integer(i));
			anneeDvdList.add(new Integer(i));
		}
	}
	@PostConstruct
	public void init() {
		zoneDvdList = Arrays.asList(ZoneDvd.values());
		fillAnneeList();
	}
	public String add() {
		id=null;
		filmDto = new FilmDto();
		selectedRealisateur=null;
		selectedAnnee=null;
		personneListBean.getActeursListModel().getTarget().clear();
		return "addFilm?faces-redirect=true";
	}
	public String delete() {
		return "deleteFilm?faces-redirect=true";
	}
	public void add(ActionEvent actionEvent) {
		logger.debug("selectedRealisateur="+selectedRealisateur.toString());
		logger.debug("selectedZone="+selectedZone);
		logger.debug("selectedRipped="+selectedRipped);
		filmDto.setAnnee(selectedAnnee);
		DvdDto dvd = new DvdDto();
		dvd.setZone(selectedZone.getId());
		dvd.setAnnee(selectedAnneeDvd);
		filmDto.setDvd(dvd);
		PersonnesFilm pf = new PersonnesFilm();
		PersonneDto real = personneService.findByPersonneId(selectedRealisateur.getId());
		RealisateurDto realisateurDto = new RealisateurDto(real);
		pf.setRealisateur(realisateurDto);
		for(PersonneDto pDto : personneListBean.getActeursListModel().getTarget()) {
			logger.debug("pDto="+pDto.toString());
			PersonneDto act = personneService.findByPersonneId(pDto.getId());
			ActeurDto acteurDto = new ActeurDto();
			acteurDto.setPersonne(act);
			pf.getActeurs().add(acteurDto);
		}
		filmDto.setPersonnesFilm(pf);
		filmDto.setRipped(selectedRipped);
		logger.debug("filmDto="+filmDto.toString());
		Film film = filmDto.fromDto();
		try {
			filmService.updateFilm(film);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Film Ajoute",  null);
	        FacesContext.getCurrentInstance().addMessage("messages", message);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "une erreur est survenue lors de l'enregistrement",  null);
	        FacesContext.getCurrentInstance().addMessage("messages", message);
		}
	}
	public String edit() {
		logger.debug("id="+id);
		try {
			filmDto = filmService.findFilmWithAllObjectGraph(id);
			selectedRealisateur = filmDto.getPersonnesFilm().getRealisateur().getPersonne();
			selectedZone = ZoneDvd.getById(filmDto.getDvd().getZone());
			selectedAnnee = filmDto.getAnnee();
			if(filmDto.getDvd().getAnnee()!=null) {
				selectedAnneeDvd = filmDto.getDvd().getAnnee();
			}
			selectedRipped = filmDto.isRipped();
			personneListBean.getActeursListModel().getTarget().clear();
			Set<ActeurDto> acteursDto = filmDto.getPersonnesFilm().getActeurs();
			for(ActeurDto acteurDto : acteursDto) {
				personneListBean.getActeursListModel().getSource().remove(acteurDto.getPersonne());
				personneListBean.getActeursListModel().getTarget().add(acteurDto.getPersonne());
			}
			Collections.sort(personneListBean.getActeursListModel().getTarget());
		} catch (Exception e) {
			logger.error(e.getMessage());
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Une erreur est survenue",  null);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return "editFilm?faces-redirect=true";
	}
	public PersonneDto getSelectedRealisateur() {
		return selectedRealisateur;
	}
	public void setSelectedRealisateur(PersonneDto selectedRealisateur) {
		this.selectedRealisateur = selectedRealisateur;
	}
	public ZoneDvd getSelectedZone() {
		return selectedZone;
	}
	public void setSelectedZone(ZoneDvd selectedZone) {
		this.selectedZone = selectedZone;
	}
	public void update(ActionEvent actionEvent) {
		logger.debug("id="+id);
		logger.debug("selectedRealisateur="+selectedRealisateur.toString());
		logger.debug("selectedZone="+selectedZone);
		
		logger.debug("selectedRipped="+selectedRipped);
		filmDto.setAnnee(selectedAnnee);
		filmDto.getDvd().setZone(selectedZone.getId());
		filmDto.getDvd().setAnnee(selectedAnneeDvd);
		PersonnesFilm pf = filmDto.getPersonnesFilm();
		RealisateurDto realisateurDto = new RealisateurDto(selectedRealisateur);
		pf.setRealisateur(realisateurDto);
		pf.getActeurs().clear();
		for(PersonneDto pDto : personneListBean.getActeursListModel().getTarget()) {
			logger.debug("pDto="+pDto.toString());
			ActeurDto acteurDto = new ActeurDto();
			acteurDto.setPersonne(pDto);
			pf.getActeurs().add(acteurDto);
		}
		filmDto.setRipped(selectedRipped);
		logger.debug("filmDto="+filmDto.toString());
		Film f = filmDto.fromDto();
		filmService.updateFilm(f);
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Film Modifie",  null);
        FacesContext.getCurrentInstance().addMessage("messages", message);
	}
	
	public List<ZoneDvd> getZoneDvdList() {
		return zoneDvdList;
	}
	public void setZoneDvdList(List<ZoneDvd> zoneDvdList) {
		this.zoneDvdList = zoneDvdList;
	}
	public List<Integer> getAnneeList() {
		return anneeList;
	}
	public void setAnneeList(List<Integer> anneeList) {
		this.anneeList = anneeList;
	}
	public List<Integer> getAnneeDvdList() {
		return anneeDvdList;
	}
	public void setAnneeDvdList(List<Integer> anneeDvdList) {
		this.anneeDvdList = anneeDvdList;
	}
	public Integer getSelectedAnnee() {
		return selectedAnnee;
	}
	public void setSelectedAnnee(Integer selectedAnnee) {
		this.selectedAnnee = selectedAnnee;
	}
	public Integer getSelectedAnneeDvd() {
		return selectedAnneeDvd;
	}
	public void setSelectedAnneeDvd(Integer selectedAnneeDvd) {
		this.selectedAnneeDvd = selectedAnneeDvd;
	}
	public boolean isSelectedRipped() {
		return selectedRipped;
	}
	public void setSelectedRipped(boolean selectedRipped) {
		this.selectedRipped = selectedRipped;
	}
	
}
