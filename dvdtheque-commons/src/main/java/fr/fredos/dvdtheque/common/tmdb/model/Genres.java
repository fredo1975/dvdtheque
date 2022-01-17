package fr.fredos.dvdtheque.common.tmdb.model;

public class Genres {
	private int id;
	private String name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Genres [id=" + id + ", name=" + name + "]";
	}
}
