package fr.fredos.dvdtheque.dao.model.object;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {
	Credentials findByName(String name);
}
