package fr.fredos.dvdtheque.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.dao.model.object.User;
import fr.fredos.dvdtheque.dao.model.repository.AuthenticatorDao;
import fr.fredos.dvdtheque.service.IAuthenticatorService;


@Service("authenticatorService")
public class AuthenticatorServiceImpl implements IAuthenticatorService {
	protected Logger logger = LoggerFactory.getLogger(AuthenticatorServiceImpl.class);
	@Autowired
    private AuthenticatorDao authenticatorDao;
	
	@Transactional(readOnly = true)
	public User authenticate(String userName, String password){
		User user = authenticatorDao.authenticate(userName, password);
		if(user!=null) {
			logger.debug(user.toString());
		}
		return user;
	}

}
