package fr.fredos.dvdtheque.rest.allocine.model;

import java.util.Objects;

public class CritiquePresseDto {
	private int id;
	private String newsSource;
	private Double rating;
	private String body;
	private String author;
	public CritiquePresseDto() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNewsSource() {
		return newsSource;
	}
	public void setNewsSource(String newsSource) {
		this.newsSource = newsSource;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CritiquePresseDto other = (CritiquePresseDto) obj;
		return id == other.id;
	}
	@Override
	public String toString() {
		return "CritiquePresseDto [id=" + id + ", newsSource=" + newsSource + ", rating=" + rating + ", body=" + body
				+ ", author=" + author + "]";
	}
	
}
