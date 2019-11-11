package fr.fredos.dvdtheque.common.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.fredos.dvdtheque.common.enums.FilmFilterCriteriaType;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;

public class FilmFilterCriteriaDto {

	private String titre;
	private Integer annee;
	private Set<FilmFilterCriteriaType> filmFilterCriteriaTypeSet;
	private final Long selectedRealisateur;
	private final Long selectedActeur;
	private final Boolean selectedRipped;
	private final Boolean selectedRippedSince;
	private final FilmOrigine selectedFilmOrigine;
	public FilmFilterCriteriaDto(final String titre, final Integer annee, final Long selectedRealisateur, final Long selectedActeur,
			final Boolean selectedRipped, final Boolean selectedRippedSince, final FilmOrigine selectedFilmOrigine) {
		super();
		this.titre = titre;
		this.annee = annee;
		this.selectedRealisateur = selectedRealisateur;
		this.selectedActeur = selectedActeur;
		this.selectedRipped = selectedRipped;
		this.selectedRippedSince = selectedRippedSince;
		this.selectedFilmOrigine = selectedFilmOrigine;
		applyFilmFilterCriteriaType();
	}
	private void applyFilmFilterCriteriaType() {
		Predicate<Boolean> filmFilterCriteriaTypeTitre = p -> StringUtils.isNotEmpty(titre);
		Predicate<Boolean> filmFilterCriteriaTypeRealisateur = p -> selectedRealisateur!=null;
		Predicate<Boolean> filmFilterCriteriaTypeAnne = p -> annee!=null;
		Predicate<Boolean> filmFilterCriteriaTypeActeur = p -> selectedActeur!=null;
		Predicate<Boolean> filmFilterCriteriaTypeRipped = p -> selectedRipped!=null;
		Predicate<Boolean> filmFilterCriteriaTypeRippedSince = p -> selectedRippedSince!=null;
		Predicate<Boolean> filmFilterCriteriaTypeFilmorigine = p -> selectedFilmOrigine!=null;
		
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
		if(filmFilterCriteriaTypeRippedSince.test(Boolean.TRUE)){
			addFilmFilterCriteriaType(FilmFilterCriteriaType.RIPPED_SINCE);
		}
		if(filmFilterCriteriaTypeFilmorigine.test(Boolean.TRUE)){
			addFilmFilterCriteriaType(FilmFilterCriteriaType.ORIGINE);
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
	public Boolean getSelectedRipped() {
		return selectedRipped;
	}
	public Boolean getSelectedRippedSince() {
		return selectedRippedSince;
	}
	public FilmOrigine getSelectedFilmOrigine() {
		return selectedFilmOrigine;
	}
}
