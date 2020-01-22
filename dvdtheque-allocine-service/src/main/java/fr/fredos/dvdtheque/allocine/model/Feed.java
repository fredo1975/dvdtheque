package fr.fredos.dvdtheque.allocine.model;

import java.util.List;

public class Feed {
	private Integer page;
	private Integer count;
	//private Results results;
	private Integer totalResults;
	private List<Movie> movieList;
	
	public Integer getPage() {
		return page;
	}
	public Integer getCount() {
		return count;
	}
	/*
	public Results getResults() {
		return results;
	}*/
	public Integer getTotalResults() {
		return totalResults;
	}
	public List<Movie> getMovieList() {
		return movieList;
	}
	@Override
	public String toString() {
		return "Feed [page=" + page + ", count=" + count + ", totalResults=" + totalResults
				+ "]";
	}
	
	
}
