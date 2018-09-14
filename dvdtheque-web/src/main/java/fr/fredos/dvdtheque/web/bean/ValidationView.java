package fr.fredos.dvdtheque.web.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class ValidationView implements Serializable{

	private static final long serialVersionUID = 1L;
	private String login;
    private String mdp;
    private String loginErrorMsg;
    private String titre;
    private String titreO;
    private Integer zone;
    private String realisateur;
    private Integer annee;
    private Integer anneeDvd;
    private Integer nom;
    private String prenom;
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	public String getLoginErrorMsg() {
		return loginErrorMsg;
	}
	public void setLoginErrorMsg(String loginErrorMsg) {
		this.loginErrorMsg = loginErrorMsg;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public String getTitreO() {
		return titreO;
	}
	public void setTitreO(String titreO) {
		this.titreO = titreO;
	}
	public Integer getZone() {
		return zone;
	}
	public void setZone(Integer zone) {
		this.zone = zone;
	}
	public String getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(String realisateur) {
		this.realisateur = realisateur;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	public Integer getAnneeDvd() {
		return anneeDvd;
	}
	public void setAnneeDvd(Integer anneeDvd) {
		this.anneeDvd = anneeDvd;
	}
	public Integer getNom() {
		return nom;
	}
	public void setNom(Integer nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
    
}
