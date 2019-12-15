package fr.fredos.dvdtheque.tmdb.model;

import java.util.List;

public class ReleaseDates {
	private Long id;
	private List<ReleaseDatesResults> results;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<ReleaseDatesResults> getResults() {
		return results;
	}
	public void setResults(List<ReleaseDatesResults> results) {
		this.results = results;
	}
	
}
