package fr.fredos.dvdtheque.web;

import java.util.HashSet;
import java.util.Set;

import fr.fredos.dvdtheque.dto.ActeurDto;
import fr.fredos.dvdtheque.dto.DvdDto;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.dto.PersonnesFilm;
import fr.fredos.dvdtheque.dto.RealisateurDto;

public class FilmTestUtils {

	public static final String TITRE_FILM = "Lorem Ipsum";
	public static final String REAL_NOM = "toto";
	public static final String REAL_PRENOM = "titi";
	public static final String ACT_NOM = "toto";
	public static final String ACT_PRENOM = "titi";
	
	public static PersonneDto buildPersonneDto(String nom,String prenom) {
		PersonneDto p = new PersonneDto();
		p.setNom(nom);
		p.setPrenom(prenom);
		return p;
	}

	public static RealisateurDto buildRealisateurDto() {
		RealisateurDto r = new RealisateurDto();
		r.setPersonne(buildPersonneDto(REAL_NOM,REAL_PRENOM));
		return r;
	}
	public static ActeurDto buildActeurDto() {
		ActeurDto acteurDto = new ActeurDto();
		acteurDto.setPersonne(buildPersonneDto(ACT_NOM, ACT_PRENOM));
		return acteurDto;
	}
	public static Set<ActeurDto> buildActeurSet() {
		Set<ActeurDto> set = new HashSet<>();
		set.add(buildActeurDto());
		return set;
	}
	public static PersonnesFilm buildPersonnesFilm() {
		PersonnesFilm pf = new PersonnesFilm();
		pf.setRealisateur(buildRealisateurDto());
		pf.setActeur(buildActeurSet());
		return pf;
	}
	public static DvdDto buildDvdDto() {
		DvdDto dvdDto = new DvdDto();
		dvdDto.setAnnee(1999);
		dvdDto.setEdition("edition");
		dvdDto.setZone(1);
		return dvdDto;
	}
	public static FilmDto buildFilmDto(String titre) {
		FilmDto film = new FilmDto();
		film.setAnnee(new Integer(1999));
		film.setRipped(false);
		film.setTitre(titre);
		film.setDvd(buildDvdDto());
		film.setPersonnesFilm(buildPersonnesFilm());
		return film;
	}
}
