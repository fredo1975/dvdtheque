package fr.fredos.dvdtheque.allocine.model;

public class Movie extends AlloCineBase{
	private Statistics statistics;
	public Statistics getStatistics() {
		return statistics;
	}
	@Override
	public String toString() {
		return "Movie [code=" + super.code + ", statistics=" + statistics + "]";
	}
	
}
