package fr.fredos.dvdtheque.service;

import fr.fredos.dvdtheque.dao.model.object.User;

public interface AuthenticatorService {
	public User authenticate(String userName, String password);
}
