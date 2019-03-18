package fr.fredos.dvdtheque.swing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.service.IAuthenticatorService;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.IPersonneService;

public class Session {
	protected final Log logger = LogFactory.getLog(Session.class);
	
	// les états des options du menu
	private boolean etatMenuLogin;
	private boolean etatMenuLogout;
	private boolean etatMenuListe;
	private boolean etatMenuNouveauFilm;
	/*
	private boolean etatMenuValiderPanier;
	private boolean etatMenuVoirPanier;
	*/
	private boolean etatMenuQuitter;
	// un message à afficher
	private String message;
	private Film film;
	private List<Film> filmList;
	private Long filmId;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected IAuthenticatorService authenticatorService;
	@Autowired
	protected IPersonneService personneService;
	//private TypePersonneFilmService typePersonneFilmService;
	private User user = null;
	private String userName = null;
	private String mdp = null;
	private List<Personne> personneList;
	
	public IFilmService getFilmService() {
		return filmService;
	}
	public IAuthenticatorService getAuthenticatorService() {
		return authenticatorService;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	public void initializeUserLoginFields(String login,String mdp){
		this.userName = login;
		this.mdp = mdp;
	}
	
	public List<Personne> getPersonneList() {
		return personneList;
	}
	public void setPersonneList(List<Personne> personneList) {
		this.personneList = personneList;
	}

	// liste de textes
	private ArrayList<String> textes = new ArrayList<String>();
	// liste d'erreurs
	private ArrayList<String> erreurs = new ArrayList<String>();
	public ArrayList<String> getErreurs() {
		return erreurs;
	}
	public ArrayList<String> getTextes() {
		return textes;
	}
	public void setErreurs(ArrayList<String> erreurs) {
		this.erreurs = erreurs;
	}
	public void setTextes(ArrayList<String> textes) {
		this.textes = textes;
	}
	public boolean isEtatMenuListe() {
		return etatMenuListe;
	}
	public void setEtatMenuListe(boolean etatMenuListe) {
		this.etatMenuListe = etatMenuListe;
	}
	public boolean isEtatMenuNouveauFilm() {
		return etatMenuNouveauFilm;
	}
	public void setEtatMenuNouveauFilm(boolean etatMenuNouveauFilm) {
		this.etatMenuNouveauFilm = etatMenuNouveauFilm;
	}
	public boolean isEtatMenuQuitter() {
		return etatMenuQuitter;
	}
	public boolean isEtatMenuLogin() {
		return etatMenuLogin;
	}
	public void setEtatMenuLogin(boolean etatMenuLogin) {
		this.etatMenuLogin = etatMenuLogin;
	}
	public boolean isEtatMenuLogout() {
		return etatMenuLogout;
	}
	public void setEtatMenuLogout(boolean etatMenuLogout) {
		this.etatMenuLogout = etatMenuLogout;
	}
	public void setEtatMenuQuitter(boolean etatMenuQuitter) {
		this.etatMenuQuitter = etatMenuQuitter;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Film getFilm() {
		return film;
	}
	public void setFilm(Film film) {
		this.film = film;
	}
	public Long getFilmId() {
		return filmId;
	}
	public void setFilmId(Long filmId) {
		this.filmId = filmId;
	}
	public List<Film> getFilmList() {
		return filmList;
	}
	public void setFilmList(List<Film> filmList) {
		this.filmList = filmList;
	}
	public IPersonneService getPersonneService() {
		return personneService;
	}
}
