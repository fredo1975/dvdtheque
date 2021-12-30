package fr.fredos.dvdtheque.batch.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genre implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private int tmdbId;
	public Genre() {
		super();
	}
	public Genre(@NotNull int tmdbId, @NotNull String name) {
		super();
		this.tmdbId = tmdbId;
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTmdbId() {
		return tmdbId;
	}
	public void setTmdbId(int tmdbId) {
		this.tmdbId = tmdbId;
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
		Genre other = (Genre) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Genre [id=" + id + ", name=" + name + ", tmdbId=" + tmdbId + "]";
	}
}
