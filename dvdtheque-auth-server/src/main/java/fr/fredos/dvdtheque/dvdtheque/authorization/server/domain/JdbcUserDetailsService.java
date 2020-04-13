package fr.fredos.dvdtheque.dvdtheque.authorization.server.domain;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.fredos.dvdtheque.dao.model.object.Credentials;
import fr.fredos.dvdtheque.dao.model.object.CredentialsRepository;

public class JdbcUserDetailsService implements UserDetailsService{
	private final CredentialsRepository credentialsRepository;

    public JdbcUserDetailsService(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credentials credentials = credentialsRepository.findByName(username);
        if(credentials == null) {
            throw new UsernameNotFoundException("User " + username + " not found in database.");
        }
        return new User(credentials.getName(), credentials.getPassword(), credentials.isEnabled(), true, true, true, credentials.getAuthorities());
    }
}
