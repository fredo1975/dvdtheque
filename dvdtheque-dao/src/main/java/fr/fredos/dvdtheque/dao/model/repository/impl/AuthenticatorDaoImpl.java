package fr.fredos.dvdtheque.dao.model.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.dao.model.repository.AuthenticatorDao;
@Repository("authenticatorDao")
public class AuthenticatorDaoImpl implements AuthenticatorDao{
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorDao.class);

	@PersistenceContext
	private EntityManager entityManager;

	public User authenticate(String userName, String password) {
		User user = null;
		Query q = this.entityManager.createQuery("from User where username = :username and password = :password");
		q.setParameter("username", userName);
		q.setParameter("password", password);
		try {
			user = (User) q.getSingleResult();
		}catch(NoResultException e) {
			logger.warn(e.getMessage());
		}
		return user;
	}
}
