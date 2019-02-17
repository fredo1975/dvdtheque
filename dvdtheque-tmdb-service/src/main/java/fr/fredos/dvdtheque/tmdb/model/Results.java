package fr.fredos.dvdtheque.tmdb.model;

public class Results {

	private Long id;
	private String title;
	private String poster_path;
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
	public String getPoster_path() {
		return poster_path;
	}
	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}
	@Override
	public String toString() {
		return "Results [id=" + id + ", title=" + title + ", poster_path=" + poster_path + "]";
	}
	
	
}
