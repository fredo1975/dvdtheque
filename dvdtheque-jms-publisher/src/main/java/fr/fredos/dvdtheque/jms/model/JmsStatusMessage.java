package fr.fredos.dvdtheque.jms.model;

import fr.fredos.dvdtheque.common.enums.JmsStatus;
import fr.fredos.dvdtheque.dao.model.object.Film;

public class JmsStatusMessage {

	private JmsStatus status;
	private Film film;
	
	public JmsStatusMessage(JmsStatus status, Film film) {
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
	public Film getFilm() {
		return film;
	}
	public void setFilm(Film film) {
		this.film = film;
	}
	
	
}
