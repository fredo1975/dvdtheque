package fr.fredos.dvdtheque.allocine.service;

import java.util.List;
import java.util.Optional;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

public interface AllocineService {
	void scrapAllAllocineFicheFilm();
	List<FicheFilm> retrieveAllFicheFilm();
	Optional<FicheFilm> retrieveFicheFilm(int id);
	List<FicheFilm> retrieveFicheFilmByTitle(String title);
	Optional<FicheFilm> findByFicheFilmId(Integer ficheFilmId);
	void removeAllFilmWithoutCritique();
	List<FicheFilm> findAllFilmWithoutCritique();
	FicheFilm saveFicheFilm(FicheFilm ficheFilm);
	List<FicheFilm> saveFicheFilmList(List<FicheFilm> ficheFilmList);
	Optional<FicheFilm> findInCacheByFicheFilmId(Integer ficheFilmId);
	Optional<FicheFilm> findInCacheByFicheFilmTitle(String title);
}
