package fr.fredos.dvdtheque.common.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.fredos.dvdtheque.common.enums.FilmFilterCriteriaType;

public class FilmFilterCriteriaDto {

	private String titre;
	private Integer annee;
	private Set<FilmFilterCriteriaType> filmFilterCriteriaTypeSet;
	private Long selectedRealisateur;
	private Long selectedActeur;
	private Boolean selectedRipped;
	
	public FilmFilterCriteriaDto(String titre, Integer annee, Long selectedRealisateur, Long selectedActeur,
			Boolean selectedRipped) {
		super();
		this.titre = titre;
		this.annee = annee;
		this.selectedRealisateur = selectedRealisateur;
		this.selectedActeur = selectedActeur;
		this.selectedRipped = selectedRipped;
		applyFilmFilterCriteriaType();
	}
	private void applyFilmFilterCriteriaType() {
		Predicate<Boolean> filmFilterCriteriaTypeTitre = p -> StringUtils.isNotEmpty(titre);
		Predicate<Boolean> filmFilterCriteriaTypeRealisateur = p -> selectedRealisateur!=null;
		Predicate<Boolean> filmFilterCriteriaTypeAnne = p -> annee!=null;
		Predicate<Boolean> filmFilterCriteriaTypeActeur = p -> selectedActeur!=null;
		Predicate<Boolean> filmFilterCriteriaTypeRipped = p -> selectedRipped!=null;
		
		if(filmFilterCriteriaTypeTitre.test(Boolean.TRUE)){
			addFilmFilterCriteriaType(FilmFilterCriteriaType.TITRE);
		}
		if(filmFilterCriteriaTypeRealisateur.test(Boolean.TRUE)){
			addFilmFilterCriteriaType(FilmFilterCriteriaType.REALISATEUR);
		}
		if(filmFilterCriteriaTypeAnne.test(Boolean.TRUE)){
			addFilmFilterCriteriaType(FilmFilterCriteriaType.ANNEE);
		}
		if(filmFilterCriteriaTypeActeur.test(Boolean.TRUE)){
			addFilmFilterCriteriaType(FilmFilterCriteriaType.ACTEUR);
		}
		if(filmFilterCriteriaTypeRipped.test(Boolean.TRUE)){
			addFilmFilterCriteriaType(FilmFilterCriteriaType.RIPPED);
		}
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	public Long getSelectedRealisateur() {
		return selectedRealisateur;
	}
	public void setSelectedRealisateur(Long selectedRealisateur) {
		this.selectedRealisateur = selectedRealisateur;
	}
	public Set<FilmFilterCriteriaType> getFilmFilterCriteriaTypeSet() {
		return filmFilterCriteriaTypeSet;
	}
	
	public void addFilmFilterCriteriaType(FilmFilterCriteriaType filmFilterCriteriaType) {
		if(CollectionUtils.isEmpty(filmFilterCriteriaTypeSet)) {
			filmFilterCriteriaTypeSet = new HashSet<>();
		}
		filmFilterCriteriaTypeSet.add(filmFilterCriteriaType);
	}
	public Long getSelectedActeur() {
		return selectedActeur;
	}
	public void setSelectedActeur(Long selectedActeur) {
		this.selectedActeur = selectedActeur;
	}
	public Boolean getSelectedRipped() {
		return selectedRipped;
	}
	public void setSelectedRipped(Boolean selectedRipped) {
		this.selectedRipped = selectedRipped;
	}
}
