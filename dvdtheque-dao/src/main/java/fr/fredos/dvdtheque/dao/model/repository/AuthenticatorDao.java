package fr.fredos.dvdtheque.dao.model.repository;

import fr.fredos.dvdtheque.dao.model.object.User;

public interface AuthenticatorDao {
	public User authenticate(String userName, String password);
}
