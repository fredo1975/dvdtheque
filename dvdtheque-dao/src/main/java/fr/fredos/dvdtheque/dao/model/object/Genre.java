package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Entity
@Table(name = "GENRE")
public class Genre implements Serializable{
	private static final long serialVersionUID = 1L;
	@Transient
	protected Logger logger = LoggerFactory.getLogger(Genre.class);
	@Id
	private int id;
	@Column(name = "NAME")
	@NotNull
	private String name;
	public Genre() {
		super();
	}
	public Genre(int id) {
		super();
		this.id = id;
	}
	public Genre(int id, @NotNull String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Genres [id=" + id + ", name=" + name + "]";
	}
}
