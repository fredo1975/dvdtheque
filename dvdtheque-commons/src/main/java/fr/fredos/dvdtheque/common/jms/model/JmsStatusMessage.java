package fr.fredos.dvdtheque.common.jms.model;

import fr.fredos.dvdtheque.common.enums.JmsStatus;

public class JmsStatusMessage<T> {

	private JmsStatus status;
	private T film;
	
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
	
	
}
