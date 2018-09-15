package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.service.PersonneService;

@ManagedBean(name="personneBean")
@ViewScoped
public class PersonneBean implements Serializable{
	protected Logger logger = LoggerFactory.getLogger(FilmListBean.class);
	private static final long serialVersionUID = 1L;
	private PersonneDto personneDto;
	private String nom;
	private String prenom;
	private PersonneDto selectedPersonneToChanged;
	@ManagedProperty(value="#{personneService}")
	protected PersonneService personneService;
	public void setPersonneService(PersonneService personneService) {
		this.personneService = personneService;
	}
	public PersonneDto getPersonneDto() {
		return personneDto;
	}
	public void setPersonneDto(PersonneDto personneDto) {
		this.personneDto = personneDto;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public PersonneDto getSelectedPersonneToChanged() {
		return selectedPersonneToChanged;
	}
	public void setSelectedPersonneToChanged(PersonneDto selectedPersonneToChanged) {
		this.selectedPersonneToChanged = selectedPersonneToChanged;
	}
	public String add() {
		personneDto = new PersonneDto();
		return "addPerson?faces-redirect=true";
	}
	public String navToModify() {
		init();
		return "modifyPerson?faces-redirect=true";
	}
	public String navToAdd() {
		init();
		return "addPerson?faces-redirect=true";
	}
	private void init() {
		nom = null;
		prenom = null;
		selectedPersonneToChanged = null;
	}
	public String modify() {
		selectedPersonneToChanged.setNom(nom);
		selectedPersonneToChanged.setPrenom(prenom);
		personneService.updatePersonne(selectedPersonneToChanged);
		return "modifyPerson?faces-redirect=true";
	}
	public void select(ValueChangeEvent valueChangeEvent) {
		PersonneDto personneDto = (PersonneDto) valueChangeEvent.getNewValue();
		logger.debug(personneDto.toString());
		nom = personneDto.getNom();
		prenom = personneDto.getPrenom();
	}
	public void add(ActionEvent actionEvent) {
		personneDto = new PersonneDto(nom,prenom);
		Personne personne = PersonneDto.fromDto(personneDto);
		personneService.savePersonne(personne);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Personne Ajoutee",  null);
        FacesContext.getCurrentInstance().addMessage("messages", message);
	}
	
}
