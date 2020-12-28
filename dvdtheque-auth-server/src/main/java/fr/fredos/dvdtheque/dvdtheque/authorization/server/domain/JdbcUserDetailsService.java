package fr.fredos.dvdtheque.dvdtheque.authorization.server.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.fredos.dvdtheque.dao.model.object.CredentialsRepository;
import fr.fredos.dvdtheque.dao.model.object.User;
@Service
public class JdbcUserDetailsService implements UserDetailsService{
	@Autowired
	private CredentialsRepository credentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	Objects.requireNonNull(username);
    	Optional<fr.fredos.dvdtheque.dao.model.object.User> possibleUser = credentialsRepository.findUserWithName(username);
    	possibleUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	User user = possibleUser.get();
    	List<SimpleGrantedAuthority> l = user.getUserRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),l);
    }
}
