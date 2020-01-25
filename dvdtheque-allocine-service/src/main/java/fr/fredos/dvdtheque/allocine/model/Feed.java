package fr.fredos.dvdtheque.allocine.model;

import java.util.List;

public class Feed {
	private Integer page;
	private Integer count;
	private Integer totalResults;
	private List<Movie> movie;
	private List<Review> review;
	
	public Integer getPage() {
		return page;
	}
	public Integer getCount() {
		return count;
	}
	public List<Review> getReview() {
		return review;
	}
	public Integer getTotalResults() {
		return totalResults;
	}
	public List<Movie> getMovie() {
		return movie;
	}
	@Override
	public String toString() {
		return "Feed [page=" + page + ", count=" + count + ", totalResults=" + totalResults + ", movie=" + movie
				+ ", review=" + review + "]";
	}
	
}
