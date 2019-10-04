package fr.fredos.dvdtheque.tmdb.model;

import java.util.List;

public class ImagesResults {
	private List<BackDrops> backdrops;
	private List<Posters> posters;
	public List<BackDrops> getBackdrops() {
		return backdrops;
	}
	public void setBackdrops(List<BackDrops> backdrops) {
		this.backdrops = backdrops;
	}
	public List<Posters> getPosters() {
		return posters;
	}
	public void setPosters(List<Posters> posters) {
		this.posters = posters;
	}
}
