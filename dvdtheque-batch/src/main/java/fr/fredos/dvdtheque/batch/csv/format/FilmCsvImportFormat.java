package fr.fredos.dvdtheque.batch.csv.format;

public class FilmCsvImportFormat {
	public FilmCsvImportFormat() {
		super();
	}
	public FilmCsvImportFormat(String realisateur, String titre, Integer zonedvd, Integer annee, String acteurs) {
		super();
		this.realisateur = realisateur;
		this.titre = titre;
		this.zonedvd = zonedvd;
		this.annee = annee;
		this.acteurs = acteurs;
	}
	private String realisateur;
	private String titre;
	private Integer zonedvd;
	private Integer annee;
	private String acteurs;
	
	public String getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(String realisateur) {
		this.realisateur = realisateur;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public Integer getZonedvd() {
		return zonedvd;
	}
	public void setZonedvd(Integer zonedvd) {
		this.zonedvd = zonedvd;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	public String getActeurs() {
		return acteurs;
	}
	public void setActeurs(String acteurs) {
		this.acteurs = acteurs;
	}
	@Override
	public String toString() {
		return "FilmCsvImportFormat [realisateur=" + realisateur + ", titre=" + titre + ", zonedvd=" + zonedvd
				+ ", annee=" + annee + ", acteurs=" + acteurs + "]";
	}
}
