package fr.fredos.dvdtheque.service.model;

import java.io.Serializable;
import java.util.List;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Genre;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class FilmListParam implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<Film> films;
	private List<Personne> realisateurs;
	private List<Personne> acteurs;
	private List<Genre> genres;
	private int realisateursLength;
	private int acteursLength;
	public List<Film> getFilms() {
		return films;
	}
	public void setFilms(List<Film> films) {
		this.films = films;
	}
	public List<Personne> getRealisateurs() {
		return realisateurs;
	}
	public void setRealisateurs(List<Personne> realisateurs) {
		this.realisateurs = realisateurs;
	}
	public List<Personne> getActeurs() {
		return acteurs;
	}
	public void setActeurs(List<Personne> acteurs) {
		this.acteurs = acteurs;
	}
	public List<Genre> getGenres() {
		return genres;
	}
	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}
	public int getRealisateursLength() {
		return realisateursLength;
	}
	public void setRealisateursLength(int realisateursLength) {
		this.realisateursLength = realisateursLength;
	}
	public int getActeursLength() {
		return acteursLength;
	}
	public void setActeursLength(int acteursLength) {
		this.acteursLength = acteursLength;
	}
	
	
}
