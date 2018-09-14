package fr.fredos.dvdtheque.batch.film.writer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.dto.ActeurDto;
import fr.fredos.dvdtheque.dto.FilmDto;
import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.dto.PersonnesFilm;
import fr.fredos.dvdtheque.dto.RealisateurDto;

public class FilmWriter implements ItemWriter<FilmDto> {
	protected Logger logger = LoggerFactory.getLogger(FilmWriter.class);
	
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	private static final String REQUEST_INSERT_DVD = "insert into DVD (ANNEE,ZONE,EDITION) values (?,?,?)";
	private static final String REQUEST_SELECT_MAX_DVD = "select max(ID) from DVD";
	private static final String REQUEST_INSERT_FILM = "insert into FILM (ANNEE,TITRE,TITRE_O,ID_DVD,RIPPED) values (?,?,?,?,?)";
	private static final String REQUEST_SELECT_TITRE_FILM = "select ID from FILM where TITRE=? and annee=?";
	private static final String REQUEST_INSERT_PERSONNE = "insert into PERSONNE(NOM,PRENOM,DATE_N) values (?,?,?)";
	private static final String REQUEST_SELECT_PERSONNE = "select * from PERSONNE where UPPER(NOM)=? and UPPER(PRENOM)=?";
	private static final String REQUEST_SELECT_ID_PERSONNE = "select ID from PERSONNE where UPPER(NOM)=? and UPPER(PRENOM)=?";
	private static final String REQUEST_INSERT_ACTEUR = "insert into ACTEUR(ID_FILM,ID_PERSONNE) values (?,?)";
	private static final String REQUEST_INSERT_REALISATEUR = "insert into REALISATEUR(ID_FILM,ID_PERSONNE) values (?,?)";
	
	private static final class PersonneMapper implements RowMapper<Personne> {
	    public Personne mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	Personne p = new Personne();
	        p.setNom(rs.getString("NOM"));
	        p.setPrenom(rs.getString("PRENOM"));
	        p.setId(rs.getInt("ID"));
	        return p;
	    }
	}
	@Override
	public void write(List<? extends FilmDto> items) throws Exception {
		for(FilmDto filmDto : items){
			logger.debug("filmDto="+filmDto.toString());
			// DVD
			final Object dvd [] = {filmDto.getDvd().getAnnee(),filmDto.getDvd().getZone(),filmDto.getDvd().getEdition()};
            jdbcTemplate.update(REQUEST_INSERT_DVD, dvd);
            int idMaxDvd = jdbcTemplate.queryForObject(REQUEST_SELECT_MAX_DVD, Integer.class);
            // FILM
            final Object film [] = {filmDto.getAnnee(),filmDto.getTitre(),filmDto.getTitreO(),idMaxDvd,false};
            jdbcTemplate.update(REQUEST_INSERT_FILM, film);
            final Object titre [] = {filmDto.getTitre(),filmDto.getAnnee()};
            int idFilm = jdbcTemplate.queryForObject(REQUEST_SELECT_TITRE_FILM, Integer.class,titre);
            PersonnesFilm pf = filmDto.getPersonnesFilm();
            Set<ActeurDto> acteurs = pf.getActeurs();
            for(ActeurDto acteurDto : acteurs){
            	PersonneDto personne = acteurDto.getPersonne();
            	final Object nomPrenom [] = {personne.getNom(),personne.getPrenom()};
            	List<Personne> l = jdbcTemplate.query(REQUEST_SELECT_PERSONNE, nomPrenom,new PersonneMapper());
            	Integer acteurId = null;
            	if(CollectionUtils.isEmpty(l)){
            		final Object personneTab [] = {personne.getNom(),personne.getPrenom(),personne.getDateN()};
                	jdbcTemplate.update(REQUEST_INSERT_PERSONNE, personneTab);
                	acteurId = jdbcTemplate.queryForObject(REQUEST_SELECT_ID_PERSONNE,Integer.class, nomPrenom);
            	}else{
            		acteurId = l.get(0).getId();
            	}
            	final Object personneFilmTab [] = {idFilm,acteurId};
            	jdbcTemplate.update(REQUEST_INSERT_ACTEUR, personneFilmTab);
            }
            RealisateurDto realisateurDto = pf.getRealisateur();
            PersonneDto personneDto = realisateurDto.getPersonne();
            final Object nomPrenom [] = {personneDto.getNom(),personneDto.getPrenom()};
        	Integer personneDtoId = null;
        	List<Personne> l = jdbcTemplate.query(REQUEST_SELECT_PERSONNE, nomPrenom,new PersonneMapper());
        	if(CollectionUtils.isEmpty(l)){
        		final Object personneTab [] = {personneDto.getNom(),personneDto.getPrenom(),personneDto.getDateN()};
            	jdbcTemplate.update(REQUEST_INSERT_PERSONNE, personneTab);
            	personneDtoId = jdbcTemplate.queryForObject(REQUEST_SELECT_ID_PERSONNE,Integer.class, nomPrenom);
        	}else{
        		personneDtoId = l.get(0).getId();
        	}
        	final Object personneFilmTab [] = {idFilm,personneDtoId};
        	jdbcTemplate.update(REQUEST_INSERT_REALISATEUR, personneFilmTab);
		}
		
	}

}
