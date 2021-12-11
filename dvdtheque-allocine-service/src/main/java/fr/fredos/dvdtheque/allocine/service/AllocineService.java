package fr.fredos.dvdtheque.allocine.service;

import java.util.List;
import java.util.Optional;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

public interface AllocineService {
	void retrieveAllocineScrapingMoviesFeed();
	List<FicheFilm> retrieveAllFicheFilm();
	Optional<FicheFilm> retrieveFicheFilm(int id);
	FicheFilm retrieveFicheFilmByTitle(String title);
}
