package fr.fredos.dvdtheque.allocine.model;

public class Statistics {
	private Double pressRating;
	private Double userRating;
	public Double getPressRating() {
		return pressRating;
	}
	public Double getUserRating() {
		return userRating;
	}
	@Override
	public String toString() {
		return "Statistics [pressRating=" + pressRating + ", userRating=" + userRating + "]";
	}
}
