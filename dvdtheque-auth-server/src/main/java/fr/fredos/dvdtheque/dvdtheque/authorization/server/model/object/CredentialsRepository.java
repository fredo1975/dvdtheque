package fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CredentialsRepository extends JpaRepository<User, Long> {
	@Query("select u from User u where u.userName = ?1")
    Optional<User> findUserWithName(String username);
}
