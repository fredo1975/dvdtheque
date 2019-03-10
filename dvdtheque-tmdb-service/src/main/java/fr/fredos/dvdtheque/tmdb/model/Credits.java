package fr.fredos.dvdtheque.tmdb.model;

import java.util.List;

public class Credits {

	private Long id;
	private List<Crew> crew;
	private List<Cast> cast;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Crew> getCrew() {
		return crew;
	}
	public void setCrew(List<Crew> crew) {
		this.crew = crew;
	}
	public List<Cast> getCast() {
		return cast;
	}
	public void setCast(List<Cast> cast) {
		this.cast = cast;
	}
	@Override
	public String toString() {
		return "Credits [id=" + id + ", crew=" + crew + ", cast=" + cast + "]";
	}
}
