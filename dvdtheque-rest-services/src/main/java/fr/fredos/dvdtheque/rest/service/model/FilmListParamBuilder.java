package fr.fredos.dvdtheque.rest.service.model;

import java.util.List;

import fr.fredos.dvdtheque.rest.dao.domain.Film;
import fr.fredos.dvdtheque.rest.dao.domain.Genre;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;

public class FilmListParamBuilder {

	public static class Builder {
		private List<Film> films;
		private List<Personne> realisateurs;
		private List<Personne> acteurs;
		private List<Genre> genres;
		private int realisateursLength;
		private int acteursLength;
		public Builder() {
        }
		
		public Builder setFilms(List<Film> films) {
			this.films = films;
			return this;
		}

		public Builder setRealisateurs(List<Personne> realisateurs) {
			this.realisateurs = realisateurs;
			return this;
		}

		public Builder setActeurs(List<Personne> acteurs) {
			this.acteurs = acteurs;
			return this;
		}

		public Builder setGenres(List<Genre> genres) {
			this.genres = genres;
			return this;
		}

		public Builder setRealisateursLength(int realisateursLength) {
			this.realisateursLength = realisateursLength;
			return this;
		}

		public Builder setActeursLength(int acteursLength) {
			this.acteursLength = acteursLength;
			return this;
		}

		public FilmListParam build() {
			FilmListParam filmListParam = new FilmListParam();
			filmListParam.setFilms(this.films);
			filmListParam.setActeurs(this.acteurs);
			filmListParam.setActeursLength(this.acteursLength);
			filmListParam.setGenres(this.genres);
			filmListParam.setRealisateurs(this.realisateurs);
			filmListParam.setRealisateursLength(this.realisateursLength);
			return filmListParam;
		}
	}
}
