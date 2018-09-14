package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.service.PersonneService;

@ManagedBean
@SessionScoped
public class PersonneListBean implements Serializable{
	protected Logger logger = LoggerFactory.getLogger(PersonneListBean.class);
	private static final long serialVersionUID = 1L;
	private List<PersonneDto> personneList;
	private Map<Integer,PersonneDto> personneDtoByIdMap;
	@ManagedProperty(value="#{personneService}")
	protected PersonneService personneService;
	public void setPersonneService(PersonneService personneService) {
		this.personneService = personneService;
	}
	private DualListModel<PersonneDto> acteursListModel;
	private List<PersonneDto> personneSelectItemList;
	@PostConstruct
	public void init() {
		handlePersonneList();
	}
	public void handlePersonneList() {
		List<PersonneDto> source = new ArrayList<PersonneDto>();
        List<PersonneDto> target = new ArrayList<PersonneDto>();
		personneList = personneService.findAllPersonne();
		personneDtoByIdMap = new HashMap<Integer,PersonneDto>(personneList.size());
		personneSelectItemList = new ArrayList<PersonneDto>(personneList.size());
		acteursListModel = new DualListModel<PersonneDto>();
		for(PersonneDto personneDto : personneList){
			personneDtoByIdMap.put(personneDto.getId(), personneDto);
			personneSelectItemList.add(personneDto);
			source.add(personneDto);
		}
		acteursListModel = new DualListModel<PersonneDto>(source, target);
	}
	public List<PersonneDto> getPersonneList() {
		return personneList;
	}
	public void setPersonneList(List<PersonneDto> personneList) {
		this.personneList = personneList;
	}
	public List<PersonneDto> getPersonneSelectItemList() {
		personneList = personneService.findAllPersonne();
		return personneSelectItemList;
	}
	public void setPersonneSelectItemList(List<PersonneDto> personneSelectItemList) {
		this.personneSelectItemList = personneSelectItemList;
	}
	public Map<Integer, PersonneDto> getPersonneDtoByIdMap() {
		return personneDtoByIdMap;
	}
	public void setPersonneDtoByIdMap(Map<Integer, PersonneDto> personneDtoByIdMap) {
		this.personneDtoByIdMap = personneDtoByIdMap;
	}
	public DualListModel<PersonneDto> getActeursListModel() {
		return acteursListModel;
	}
	public void setActeursListModel(DualListModel<PersonneDto> acteursListModel) {
		this.acteursListModel = acteursListModel;
	}
}
