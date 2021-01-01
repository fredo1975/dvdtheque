package fr.fredos.dvdtheque.dvdtheque.authorization.server.model.repository;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.User;

public interface AuthenticatorDao {
	public User authenticate(String userName, String password);
	public User saveUser(User user);
}
