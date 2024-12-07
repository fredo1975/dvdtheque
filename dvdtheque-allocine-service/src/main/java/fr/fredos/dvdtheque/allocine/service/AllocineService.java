package fr.fredos.dvdtheque.allocine.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import fr.fredos.dvdtheque.allocine.domain.FicheFilm;

public interface AllocineService {
	void scrapAllAllocineFicheFilm();
	List<FicheFilm> retrieveAllFicheFilm();
	Optional<FicheFilm> retrieveFicheFilm(int id);
	List<FicheFilm> retrieveFicheFilmByTitle(String title);
	Optional<FicheFilm> findByFicheFilmId(Integer ficheFilmId);
	FicheFilm saveFicheFilm(FicheFilm ficheFilm);
	List<FicheFilm> saveFicheFilmList(List<FicheFilm> ficheFilmList);
	Optional<FicheFilm> findInCacheByFicheFilmId(Integer ficheFilmId);
	Optional<List<FicheFilm>> findInCacheByFicheFilmTitle(String title);
	Page<FicheFilm> paginatedSarch(String query, Integer offset, Integer limit, String sort);
}
