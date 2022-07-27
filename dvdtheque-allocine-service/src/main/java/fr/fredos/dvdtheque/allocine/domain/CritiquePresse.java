package fr.fredos.dvdtheque.allocine.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "critiquepresse")
public class CritiquePresse implements Serializable{
	private static final long serialVersionUID = -5043813433232666154L;
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
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiche_film_id") // this is foreign key in comments table
    private FicheFilm ficheFilm;
	
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
	public FicheFilm getFicheFilm() {
		return ficheFilm;
	}
	public void setFicheFilm(FicheFilm ficheFilm) {
		this.ficheFilm = ficheFilm;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(newsSource);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CritiquePresse other = (CritiquePresse) obj;
		return Objects.equals(newsSource, other.newsSource);
	}
	@Override
	public String toString() {
		return "CritiquePresse [id=" + id + ", newsSource=" + newsSource + ", rating=" + rating
				+ ", body=" + body + ", author=" + author + "]";
	}
	
}
