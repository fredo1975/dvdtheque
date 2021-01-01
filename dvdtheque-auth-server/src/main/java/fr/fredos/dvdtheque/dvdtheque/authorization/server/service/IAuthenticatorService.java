package fr.fredos.dvdtheque.dvdtheque.authorization.server.service;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.User;

public interface IAuthenticatorService {
	public User authenticate(final String userName,final String password);
	public User saveUser(final String userName, final String password);
}
