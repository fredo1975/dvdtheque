package fr.fredos.dvdtheque.allocine.model;

public class Review extends AlloCineBase{
	private Double rating;
	private String body;
	private String author;
	private Type type;
	private NewsSource newsSource;
	public Integer getCode() {
		return code;
	}
	public Double getRating() {
		return rating;
	}
	public String getBody() {
		return body;
	}
	public String getAuthor() {
		return author;
	}
	public NewsSource getNewsSource() {
		return newsSource;
	}
	public Type getType() {
		return type;
	}
	@Override
	public String toString() {
		return "Review [code=" + code + ", rating=" + rating + ", body=" + body + ", author=" + author + ", type="
				+ type + ", newsSource=" + newsSource + "]";
	}
	
}
