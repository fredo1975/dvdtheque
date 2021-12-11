package fr.fredos.dvdtheque.allocine.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CritiquePresse {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
	@Column(name="filmId")
	private String filmId;
	@Column(name="newsSource")
	private String newsSource;
	@Column(name="rating")
	private Double rating;
	@Column(name="body",columnDefinition="TEXT")
	private String body;
	@Column(name="author")
	private String author;
	@ManyToOne
    @JoinColumn(name ="ficheFilmId")
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
	public String getFilmId() {
		return filmId;
	}
	public void setFilmId(String filmId) {
		this.filmId = filmId;
	}
	
	public FicheFilm getFicheFilm() {
		return ficheFilm;
	}
	public void setFicheFilm(FicheFilm ficheFilm) {
		this.ficheFilm = ficheFilm;
	}
	@Override
	public String toString() {
		return "CritiquePresse [id=" + id + ", filmId=" + filmId + ", newsSource=" + newsSource + ", rating=" + rating
				+ ", body=" + body + ", author=" + author + ", ficheFilm=" + ficheFilm + "]";
	}
	
}
