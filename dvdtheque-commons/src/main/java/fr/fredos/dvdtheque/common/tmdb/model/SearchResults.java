package fr.fredos.dvdtheque.common.tmdb.model;

import java.util.List;

public class SearchResults {
	private List<Results> results;
	private Integer total_results;
	private Integer page;
	private Integer total_pages;
	public List<Results> getResults() {
		return results;
	}
	public void setResults(List<Results> results) {
		this.results = results;
	}
	public Integer getTotal_results() {
		return total_results;
	}
	public void setTotal_results(Integer total_results) {
		this.total_results = total_results;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getTotal_pages() {
		return total_pages;
	}
	public void setTotal_pages(Integer total_pages) {
		this.total_pages = total_pages;
	}
	@Override
	public String toString() {
		return "SearchResults [results=" + results + ", total_results=" + total_results + ", page=" + page
				+ ", total_pages=" + total_pages + "]";
	}
}
