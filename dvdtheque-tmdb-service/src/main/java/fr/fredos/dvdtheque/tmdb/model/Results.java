package fr.fredos.dvdtheque.tmdb.model;

import java.util.List;

public class Results {
	private Long id;
	private String title;
	private String original_title;
	private String poster_path;
	private String release_date;
	private String overview;
	private int runtime;
	private List<Genres> genres;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOriginal_title() {
		return original_title;
	}
	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}
	public String getPoster_path() {
		return poster_path;
	}
	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}
	public String getRelease_date() {
		return release_date;
	}
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public int getRuntime() {
		return runtime;
	}
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}
	public List<Genres> getGenres() {
		return genres;
	}
	public void setGenres(List<Genres> genres) {
		this.genres = genres;
	}
	@Override
	public String toString() {
		return "Results [id=" + id + ", title=" + title + ", original_title=" + original_title + ", poster_path="
				+ poster_path + ", release_date=" + release_date + ", overview=" + overview + ", runtime=" + runtime
				+ ", genres=" + genres + "]";
	}
}
