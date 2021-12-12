package fr.fredos.dvdtheque.batch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CritiquePresse {
	private int id;
	private String filmId;
	private String newsSource;
	private Double rating;
	private String body;
	private String author;
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
