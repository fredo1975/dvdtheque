package fr.fredos.dvdtheque.service.dto;

import java.util.HashSet;
import java.util.Set;

import fr.fredos.dvdtheque.common.dto.NewActeurDto;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class FilmUtils {

	public static final String TITRE_FILM = "Lorem Ipsum";
	public static final String TITRE_FILM_UPDATED = "Lorem Ipsum updated";
	public static final String REAL_NOM = "toto titi";
	public static final String ACT1_NOM = "tata tutu";
	public static final String ACT2_NOM = "toitoi tuitui";
	public static final String ACT3_NOM = "tuotuo tmitmi";
	public final static String MAX_REALISATEUR_ID_SQL = "select max(p.id) from PERSONNE p inner join REALISATEUR r on r.ID_PERSONNE=p.ID";
	public final static String MAX_ACTEUR_ID_SQL = "select max(p.id) from PERSONNE p inner join ACTEUR a on a.ID_PERSONNE=p.ID";
	public final static String MAX_PERSONNE_ID_SQL = "select max(id) from PERSONNE";
	public static NewActeurDto buildNewActeurDto() {
		NewActeurDto newActeurDto = new NewActeurDto();
		newActeurDto.setNom(ACT1_NOM);
		return newActeurDto;
	}
	public static Personne buildPersonne(Integer idPersonne) {
		Personne p = new Personne();
		p.setId(idPersonne);
		return p;
	}
	public static Personne buildPersonne(final String nom) {
		Personne p = new Personne();
		p.setNom(nom);
		return p;
	}
	
	public static Dvd buildDvd(Integer annee) {
		Dvd dvd = new Dvd();
		dvd.setAnnee(annee);
		dvd.setEdition("edition");
		dvd.setZone(1);
		return dvd;
	}
	public static Set<Personne> buildActeurs(final Integer idAct1,final Integer idAct2,final Integer idAct3){
		Set<Personne> acteurs = new HashSet<>();
		acteurs.add(buildPersonne(idAct1));
		if(idAct2!=null) {
			acteurs.add(buildPersonne(idAct2));
		}
		if(idAct3!=null) {
			acteurs.add(buildPersonne(idAct3));
		}
		return acteurs;
	}
	public static Set<Personne> buildRealisateurs(final Integer idRealisateur){
		Set<Personne> realisateurs = new HashSet<>();
		realisateurs.add(buildPersonne(idRealisateur));
		return realisateurs;
	}
	public static Film buildFilm(final String titre,
			final Integer annee,
			final Integer idRealisateur,
			final Integer idAct1,
			final Integer idAct2,
			final Integer idAct3) {
		Film film = new Film();
		film.setAnnee(annee);
		film.setRipped(true);
		film.setTitre(titre);
		film.setDvd(buildDvd(annee));
		film.setRealisateurs(buildRealisateurs(idRealisateur));
		film.setActeurs(buildActeurs(idAct1,idAct2,idAct3));
		film.setTmdbId(new Long(100));
		return film;
	}
}
