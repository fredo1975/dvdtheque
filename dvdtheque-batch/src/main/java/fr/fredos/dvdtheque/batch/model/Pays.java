package fr.fredos.dvdtheque.batch.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pays implements Serializable {
	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;
	private String lib;
	private String i18n;
	
	public Pays() {
		super();
	}
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer _id) {
		this.id = _id;
	}
	public String getLib() {
		return lib;
	}
	public void setLib(String lib) {
		this.lib = lib;
	}
	public String getI18n() {
		return i18n;
	}
	public void setI18n(String i18n) {
		this.i18n = i18n;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pays other = (Pays) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public String toString() {
		return "Pays [id=" + id + ", lib=" + lib + ", i18n=" + i18n + "]";
	}
}
