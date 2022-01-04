package fr.fredos.dvdtheque.allocine.service;

import java.util.List;
import java.util.Optional;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

public interface AllocineService {
	void retrieveAllocineScrapingFicheFilm();
	List<FicheFilm> retrieveAllFicheFilm();
	Optional<FicheFilm> retrieveFicheFilm(int id);
	FicheFilm retrieveFicheFilmByTitle(String title);
	Optional<FicheFilm> retrievefindByFicheFilmId(Integer ficheFilmId);
	void removeAllFilmWithoutCritique();
	List<FicheFilm> findAllFilmWithoutCritique();
	FicheFilm saveFicheFilm(FicheFilm ficheFilm);
}
