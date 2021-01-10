package fr.fredos.dvdtheque.dvdtheque.authorization.server.model.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>{
	@Query("select r from Role r where r.name = ?1")
	Optional<Role> findByName(String name);
}
