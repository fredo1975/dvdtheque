package fr.fredos.dvdtheque.dvdtheque.authorization.server.domain;

public class JdbcUserDetailsService /*implements UserDetailsService*/{
	/*
	@Autowired
	private CredentialsRepository credentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credentials credentials = credentialsRepository.findByName(username);
        if(credentials == null) {
            throw new UsernameNotFoundException("User " + username + " not found in database.");
        }
        return new User(credentials.getName(), credentials.getPassword(), credentials.isEnabled(), true, true, true, credentials.getAuthorities());
    }*/
}
