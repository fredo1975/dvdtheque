package fr.fredos.dvdtheque.rest.service.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CritiquePresse {
	private String newsSource;
	private Double rating;
	private String body;
	private String author;
	public CritiquePresse() {
		super();
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
		return "CritiquePresse [newsSource=" + newsSource + ", rating=" + rating + ", body=" + body + ", author="
				+ author + "]";
	}
}
