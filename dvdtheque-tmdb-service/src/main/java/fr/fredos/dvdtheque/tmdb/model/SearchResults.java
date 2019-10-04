package fr.fredos.dvdtheque.tmdb.model;

import java.util.List;

public class SearchResults {
	private List<Results> results;
	public List<Results> getResults() {
		return results;
	}
	public void setResults(List<Results> results) {
		this.results = results;
	}
	@Override
	public String toString() {
		return "SearchResults [results=" + results + "]";
	}
	
}
