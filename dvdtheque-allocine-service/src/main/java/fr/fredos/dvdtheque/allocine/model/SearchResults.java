package fr.fredos.dvdtheque.allocine.model;

public class SearchResults {
	private Feed feed;
	public Feed getFeed() {
		return feed;
	}
	@Override
	public String toString() {
		return "SearchResults [feed=" + feed + "]";
	}
}
