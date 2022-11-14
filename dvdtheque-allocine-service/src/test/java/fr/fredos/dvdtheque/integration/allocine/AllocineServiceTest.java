package fr.fredos.dvdtheque.integration.allocine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.allocine.AllocineServiceApplication;
import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.integration.config.HazelcastConfiguration;
import fr.fredos.dvdtheque.allocine.service.AllocineService;


@SpringBootTest(classes = {HazelcastConfiguration.class,AllocineServiceApplication.class})
@ActiveProfiles("test")
public class AllocineServiceTest {
	@MockBean
	JwtDecoder jwtDecoder;
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceTest.class);
	@Autowired
	private AllocineService allocineService;
	/*
	private FicheFilm saveFilm() {
		FicheFilm ficheFilm = new FicheFilm();
		ficheFilm.setTitle("title");
		ficheFilm.setAllocineFilmId(1);
		ficheFilm.setPageNumber(1);
		ficheFilm.setUrl("url");
		CritiquePresse cp = new CritiquePresse();
		cp.setAuthor("author1");
		cp.setBody("body1");
		cp.setNewsSource("source1");
		cp.setRating(4d);
		cp.setFicheFilm(ficheFilm);
		ficheFilm.addCritiquePresse(cp);
		FicheFilm ficheFilmSaved = allocineService.saveFicheFilm(ficheFilm);
		assertNotNull(ficheFilmSaved);
		return ficheFilmSaved;
	}*/
	
    @Test
    @Transactional
    //@Disabled
    public void retrieveAllocineScrapingFicheFilmTest() throws IOException {
    	/*
		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "user").build();
		Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read");
		JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
    		*/
    	allocineService.scrapAllAllocineFicheFilm();
    	TestTransaction.flagForCommit();
		List<FicheFilm> allFicheFilmFromPageRetrievedFromDb = allocineService.retrieveAllFicheFilm();
		//assertEquals(28,allFicheFilmFromPageRetrievedFromDb.size());
		assertTrue(allFicheFilmFromPageRetrievedFromDb.size()>10);
		FicheFilm ficheFilm0 = allFicheFilmFromPageRetrievedFromDb.get(0);
		Optional<FicheFilm> optionalFicheFilmRetrievedFromCache = allocineService.findInCacheByFicheFilmId(ficheFilm0.getAllocineFilmId());
		assertEquals(optionalFicheFilmRetrievedFromCache.get(),ficheFilm0);
		
		Optional<FicheFilm> optionalFicheFilmRetrievedFromCache2 = allocineService.findInCacheByFicheFilmId(allFicheFilmFromPageRetrievedFromDb.get(allFicheFilmFromPageRetrievedFromDb.size()-1).getAllocineFilmId());
		assertEquals(optionalFicheFilmRetrievedFromCache2.get(),allFicheFilmFromPageRetrievedFromDb.get(allFicheFilmFromPageRetrievedFromDb.size()-1));
		List<FicheFilm> ficheFilmListDbRetrieved0 = allocineService.retrieveFicheFilmByTitle(ficheFilm0.getTitle());
		assertNotNull(ficheFilmListDbRetrieved0);
		Optional<List<FicheFilm>> ficheFilmCacheRetrievd0 = allocineService.findInCacheByFicheFilmTitle(ficheFilmListDbRetrieved0.get(0).getTitle());
		assertEquals(ficheFilmCacheRetrievd0.get().get(0),ficheFilm0);
    }
}
