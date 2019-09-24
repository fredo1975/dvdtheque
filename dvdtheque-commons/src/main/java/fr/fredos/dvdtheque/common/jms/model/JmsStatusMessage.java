package fr.fredos.dvdtheque.common.jms.model;

import java.io.Serializable;

import fr.fredos.dvdtheque.common.enums.JmsStatus;

public class JmsStatusMessage<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	private JmsStatus status;
	private T film;
	
	public JmsStatusMessage() {
		super();
	}
	public JmsStatusMessage(JmsStatus status, T film) {
		super();
		this.status = status;
		this.film = film;
	}
	public JmsStatus getStatus() {
		return status;
	}
	public void setStatus(JmsStatus status) {
		this.status = status;
	}
	public T getFilm() {
		return film;
	}
	public void setFilm(T film) {
		this.film = film;
	}
	@Override
	public String toString() {
		return "JmsStatusMessage [status=" + status + ", film=" + film + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((film == null) ? 0 : film.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		JmsStatusMessage other = (JmsStatusMessage) obj;
		if (film == null) {
			if (other.film != null)
				return false;
		} else if (!film.equals(other.film))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	
}
