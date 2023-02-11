package fr.fredos.dvdtheque.rest.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.fredos.dvdtheque.rest.dao.domain.Dvd;

public interface DvdDao extends JpaRepository<Dvd, Long>, JpaSpecificationExecutor<Dvd>{

}
