package fr.fredos.dvdtheque.common.tmdb.model;

import java.util.List;

public class ReleaseDatesResults {

	private String iso_3166_1;

	private List<ReleaseDatesResultsValues> release_dates;
	
	public String getIso_3166_1() {
		return iso_3166_1;
	}

	public void setIso_3166_1(String iso_3166_1) {
		this.iso_3166_1 = iso_3166_1;
	}

	public List<ReleaseDatesResultsValues> getRelease_dates() {
		return release_dates;
	}

	public void setRelease_dates(List<ReleaseDatesResultsValues> release_dates) {
		this.release_dates = release_dates;
	}
	
	
}
