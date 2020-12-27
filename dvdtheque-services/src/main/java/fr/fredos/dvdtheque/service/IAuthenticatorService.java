package fr.fredos.dvdtheque.service;

import fr.fredos.dvdtheque.dao.model.object.User;

public interface IAuthenticatorService {
	public User authenticate(final String userName,final String password);
	public User saveUser(final String userName, final String password);
}
