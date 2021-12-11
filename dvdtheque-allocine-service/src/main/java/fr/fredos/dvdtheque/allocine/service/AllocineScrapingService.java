package fr.fredos.dvdtheque.allocine.service;

import java.util.List;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

public interface AllocineScrapingService {
	void retrieveAllocineScrapingMoviesFeed();
	List<FicheFilm> retrieveAllFicheFilm();
}
