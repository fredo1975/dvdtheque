package fr.fredos.dvdtheque.dvdtheque.authorization.server.service;

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

import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.User;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.repository.UserRepository;
@Service
public class JdbcUserDetailsService implements UserDetailsService{
	@Autowired
	private UserRepository credentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	Objects.requireNonNull(username);
    	Optional<fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.User> possibleUser = credentialsRepository.findUserWithName(username);
    	possibleUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	User user = possibleUser.get();
    	List<SimpleGrantedAuthority> l = user.getUserRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),l);
    }
}
