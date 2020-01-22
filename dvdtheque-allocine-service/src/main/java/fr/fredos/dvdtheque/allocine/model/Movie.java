package fr.fredos.dvdtheque.allocine.model;

public class Movie {
	private Integer code;
	private Statistics statistics;
	public Integer getCode() {
		return code;
	}
	public Statistics getStatistics() {
		return statistics;
	}
	@Override
	public String toString() {
		return "Movie [code=" + code + ", statistics=" + statistics + "]";
	}
	
}
