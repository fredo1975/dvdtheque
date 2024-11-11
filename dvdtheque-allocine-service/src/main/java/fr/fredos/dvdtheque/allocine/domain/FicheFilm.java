package fr.fredos.dvdtheque.allocine.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "fichefilm")
public class FicheFilm implements Serializable{
	private static final long serialVersionUID = -897598306740993505L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	@Column(name="allocine_film_id")
	private int allocineFilmId;
	@Column(name="url")
	private String url;
	@Column(name="page_number")
	private int pageNumber;
	@Column(name="title")
	private String title;
	@OneToMany(mappedBy = "ficheFilm",cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CritiquePresse> critiquePresse = new HashSet<>();
	@Column(name="creation_date")
	private LocalDateTime creationDate;
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	public FicheFilm() {
		super();
	}
	public FicheFilm(String title,int allocineFilmId, String url,int pageNumber) {
		super();
		this.title = title;
		this.allocineFilmId = allocineFilmId;
		this.url = url;
		this.pageNumber = pageNumber;
		this.creationDate = LocalDateTime.now();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAllocineFilmId() {
		return allocineFilmId;
	}
	public void setAllocineFilmId(int allocineFilmId) {
		this.allocineFilmId = allocineFilmId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Set<CritiquePresse> getCritiquePresse() {
		return critiquePresse;
	}
	public void setCritiquePresse(Set<CritiquePresse> critiquePresse) {
		this.critiquePresse = critiquePresse;
	}
	public void addCritiquePresse(CritiquePresse critique) {
		this.critiquePresse.add(critique);
	}
	@Override
	public int hashCode() {
		return Objects.hash(allocineFilmId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FicheFilm other = (FicheFilm) obj;
		return Objects.equals(allocineFilmId, other.allocineFilmId);
	}
	@Override
	public String toString() {
		return "FicheFilm [id=" + id + ", allocineFilmId=" + allocineFilmId + ", url=" + url + ", pageNumber="
				+ pageNumber + ", title=" + title + ", critiquePresse=" + critiquePresse + ", creationDate="
				+ creationDate + "]";
	}
}
