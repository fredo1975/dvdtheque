package fr.fredos.dvdtheque.service.dto;

import java.util.HashSet;
import java.util.Set;

import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class FilmUtils {

	public static final String TITRE_FILM = "Lorem Ipsum";
	public static final String REAL_NOM = "toto";
	public static final String REAL_PRENOM = "titi";
	public static final String ACT1_NOM = "tata";
	public static final String ACT1_PRENOM = "tutu";
	public static final String ACT2_NOM = "toitoi";
	public static final String ACT2_PRENOM = "tuitui";
	public static final String ACT3_NOM = "tuotuo";
	public static final String ACT3_PRENOM = "tmitmi";
	
	public static Personne buildPersonne(String nom,String prenom) {
		Personne p = new Personne();
		p.setNom(nom);
		p.setPrenom(prenom);
		return p;
	}
	
	public static Dvd buildDvd() {
		Dvd dvd = new Dvd();
		dvd.setAnnee(1999);
		dvd.setEdition("edition");
		dvd.setZone(1);
		return dvd;
	}
	public static Set<Personne> buildActeurs(){
		Set<Personne> acteurs = new HashSet<>();
		acteurs.add(buildPersonne(ACT1_NOM, ACT1_PRENOM));
		acteurs.add(buildPersonne(ACT2_NOM, ACT2_PRENOM));
		acteurs.add(buildPersonne(ACT3_NOM, ACT3_PRENOM));
		return acteurs;
	}
	public static Set<Personne> buildRealisateurs(){
		Set<Personne> acteurs = new HashSet<>();
		acteurs.add(buildPersonne(REAL_NOM, REAL_PRENOM));
		return acteurs;
	}
	public static Film buildFilm(String titre) {
		Film film = new Film();
		film.setAnnee(new Integer(1999));
		film.setRipped(false);
		film.setTitre(titre);
		film.setDvd(buildDvd());
		film.setRealisateurs(buildRealisateurs());
		film.setActeurs(buildActeurs());
		return film;
	}
}
