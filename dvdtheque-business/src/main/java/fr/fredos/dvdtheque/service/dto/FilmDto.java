package fr.fredos.dvdtheque.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;

public class FilmDto implements Serializable {

	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(getClass());
	// primary key
	private java.lang.Integer id;
	// fields
	private Integer annee;
	private String titre;
	private String titreO;
	private DvdDto dvd;
	private PersonnesFilm personnesFilm;
	private boolean ripped;
	private String posterPath;
	private Long tmdbId;
	/*
	private Set<ActeurDto> acteurs = new HashSet<ActeurDto>(0); 
	private Set<RealisateurDto> realisateurs= new HashSet<RealisateurDto>(0);
	*/
	public FilmDto() {
		super();
		personnesFilm = new PersonnesFilm();
	}
	public FilmDto(Integer id, Integer annee, String titre, String titreO) {
		super();
		this.id = id;
		this.annee = annee;
		this.titre = titre;
		this.titreO = titreO;
		personnesFilm = new PersonnesFilm();
	}
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public String getTitreO() {
		return titreO;
	}
	public void setTitreO(String titreO) {
		this.titreO = titreO;
	}
	public DvdDto getDvd() {
		return dvd;
	}
	public void setDvd(DvdDto dvd) {
		this.dvd = dvd;
	}
	public PersonnesFilm getPersonnesFilm() {
		return personnesFilm;
	}
	public void setPersonnesFilm(PersonnesFilm personnesFilm) {
		this.personnesFilm = personnesFilm;
	}
	public boolean isRipped() {
		return ripped;
	}
	public void setRipped(boolean ripped) {
		this.ripped = ripped;
	}
	public String getPosterPath() {
		return posterPath;
	}
	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}
	public Long getTmdbId() {
		return tmdbId;
	}
	public void setTmdbId(Long tmdbId) {
		this.tmdbId = tmdbId;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).
	       append("id", id).
	       append("annee", annee).
	       append("titre", titre).
	       append("titreO", titreO).
	       append("dvd", dvd).
	       append("personnesFilm", personnesFilm).
	       append("ripped", ripped).
	       append("posterPath", posterPath).
	       append("tmdbId", tmdbId).
	       toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilmDto other = (FilmDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public static FilmDto toDto(Film film){
		FilmDto filmDto = new FilmDto();
		filmDto.setAnnee(film.getAnnee());
		filmDto.setDvd(DvdDto.toDto(film.getDvd()));
		filmDto.setId(film.getId());
		filmDto.setTitre(film.getTitre());
		filmDto.setTitreO(film.getTitreO());
		filmDto.getPersonnesFilm().addActeur(film.getActeurs());
		if(!CollectionUtils.isEmpty(film.getRealisateurs())){
			filmDto.getPersonnesFilm().setRealisateur(RealisateurDto.toDto(film.getRealisateurs().iterator().next()));
		}
		filmDto.setRipped(film.isRipped());
		filmDto.setPosterPath(film.getPosterPath());
		filmDto.setTmdbId(film.getTmdbId());
		return filmDto;
	}
	
	public Film fromDto(){
		Film film = new Film();
		film.setDvd(DvdDto.fromDto(this.getDvd()));
		film.setAnnee(this.getAnnee());
		film.setTitre(this.getTitre());
		film.setTitreO(this.getTitreO());
		film.setId(this.getId());
		Set<Personne> acteurs = new HashSet<Personne>();
		Set<Personne> realisateurs = new HashSet<Personne>();
		for(ActeurDto acteurDto : this.getPersonnesFilm().getActeurs()){
			acteurs.add(PersonneDto.fromDto(acteurDto.getPersonne()));
		}
		film.setActeurs(acteurs);
		realisateurs.add(PersonneDto.fromDto(this.getPersonnesFilm().getRealisateur().getPersonne()));
		film.setRealisateurs(realisateurs);
		film.setRipped(this.isRipped());
		film.setPosterPath(this.getPosterPath());
		film.setTmdbId(this.getTmdbId());
		return film;
	}
	
	public String getPrintRealisateur() {
		return this.getPersonnesFilm().getRealisateur().getPersonne().getPrenom()+" "+this.getPersonnesFilm().getRealisateur().getPersonne().getNom();
	}
	public String getPrintActeurs() {
		StringBuilder sb = new StringBuilder();
		if(!CollectionUtils.isEmpty(this.getPersonnesFilm().getActeurs())) {
			for(ActeurDto acteurDto : this.getPersonnesFilm().getActeurs()){
				sb.append(acteurDto.getPersonne().getPrenom()+" "+acteurDto.getPersonne().getNom()+" - ");
			}
		}
		return StringUtils.removeEnd(sb.toString(), " - ");
	}
	
}
