package fr.fredos.dvdtheque.allocine.domain;

public class Page {
	private Integer numPage = 1;
	public Page(Integer numPage) {
		super();
		this.numPage = numPage;
	}
	public synchronized Integer getNumPage() {
		return numPage;
	}
	public synchronized void setNumPage(Integer numPage) {
		this.numPage = numPage;
	}
	@Override
	public String toString() {
		return "Page [numPage=" + numPage + "]";
	}
}
