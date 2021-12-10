package fr.fredos.dvdtheque.allocine.scraping.model;

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
	public String toString() {
		return "CritiquePresse [newsSource=" + newsSource + ", rating=" + rating + ", body=" + body + ", author="
				+ author + "]";
	}
	
}
