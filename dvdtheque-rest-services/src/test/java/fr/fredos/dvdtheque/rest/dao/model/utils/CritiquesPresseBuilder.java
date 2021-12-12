package fr.fredos.dvdtheque.rest.dao.model.utils;

import fr.fredos.dvdtheque.rest.dao.domain.CritiquesPresse;

public class CritiquesPresseBuilder {

	public static class Builder {
		private Long id;
		private Integer code;
		private String nomSource;
		private String auteur;
		private String critique;
		private Double note;
		
		public Builder(Long id) {
            this.id = id;
        }
		public Builder setCode(Integer code) {
			this.code = code;
			return this;
		}
		public Builder setNomSource(String nomSource) {
			this.nomSource = nomSource;
			return this;
		}
		public Builder setAuteur(String auteur) {
			this.auteur = auteur;
			return this;
		}
		public Builder setCritique(String critique) {
			this.critique = critique;
			return this;
		}
		public Builder setNote(Double note) {
			this.note = note;
			return this;
		}
		public CritiquesPresse build() {
			CritiquesPresse critiquesPresse = new CritiquesPresse();
			critiquesPresse.setCode(this.code);
			critiquesPresse.setNomSource(this.nomSource);
			critiquesPresse.setAuteur(this.auteur);
			critiquesPresse.setCritique(this.critique);
			critiquesPresse.setNote(this.note);
			return critiquesPresse;
		}
	}
}
