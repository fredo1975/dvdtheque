package fr.fredos.dvdtheque.allocine.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "critique_presse")
public class CritiquePresse {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	@Column(name="news_source")
	private String newsSource;
	@Column(name="rating")
	private Double rating;
	@Column(name="body",columnDefinition="TEXT")
	private String body;
	@Column(name="author")
	private String author;
	
	public CritiquePresse() {
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
	public String toString() {
		return "CritiquePresse [id=" + id + ", newsSource=" + newsSource + ", rating=" + rating
				+ ", body=" + body + ", author=" + author + "]";
	}
	
}
