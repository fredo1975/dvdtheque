package fr.fredos.dvdtheque.integration.allocine;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.allocine.AllocineServiceApplication;
import fr.fredos.dvdtheque.allocine.domain.CritiquePresse;
import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.service.AllocineService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AllocineServiceApplication.class})
@ActiveProfiles("test")
@Transactional
public class AllocineServiceTest {
	@MockBean
	JwtDecoder jwtDecoder;
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceTest.class);
	private static final String ALLOCINE_FIULM_ID_289301 = "289301";
	private static final String ALLOCINE_FIULM_ID_289301_TITLE = "Les Bodin's en Thaïlande";
	private static final String ALLOCINE_FIULM_ID_136316 = "136316";
	private static final String ALLOCINE_FIULM_ID_136316_TITLE = "Les Eternels";
	@Autowired
    AllocineService allocineService;
	
	private FicheFilm saveFilm() {
		FicheFilm ficheFilm = new FicheFilm();
		ficheFilm.setTitle("title");
		ficheFilm.setAllocineFilmId(1);
		ficheFilm.setPageNumber(1);
		ficheFilm.setUrl("url");
		/*
		CritiquePresse cp = new CritiquePresse();
		cp.setAuthor("author1");
		cp.setBody("body1");
		cp.setNewsSource("source1");
		cp.setRating(4d);
		cp.setFicheFilm(ficheFilm);
		ficheFilm.addCritiquePresse(cp);
		*/
		FicheFilm ficheFilmSaved = allocineService.saveFicheFilm(ficheFilm);
		assertNotNull(ficheFilmSaved);
		return ficheFilmSaved;
	}
	
	@Test
	public void removeAllFilmWithoutCritique() {
		FicheFilm ficheFilmSaved = saveFilm();
		allocineService.removeAllFilmWithoutCritique();
		List<FicheFilm> allFilms = allocineService.retrieveAllFicheFilm();
		assertNull(allFilms);
	}
    
    @Test
    //@Disabled
    public void retrieveAllocineScrapingFicheFilmTest() throws IOException {
    	/*
		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "user").build();
		Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read");
		JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
    		*/
    	allocineService.retrieveAllocineScrapingFicheFilm();
    	TestTransaction.flagForCommit();
		List<FicheFilm> allFicheFilmFromPageRetrievedFromDb = allocineService.retrieveAllFicheFilm();
		assertEquals(30,allFicheFilmFromPageRetrievedFromDb.size());
		assertEquals(ALLOCINE_FIULM_ID_289301,allFicheFilmFromPageRetrievedFromDb.get(0).getAllocineFilmId());
		assertEquals(ALLOCINE_FIULM_ID_289301_TITLE,allFicheFilmFromPageRetrievedFromDb.get(0).getTitle());
		logger.info("critique presses from {} are {}",allFicheFilmFromPageRetrievedFromDb.get(0).getTitle(),allFicheFilmFromPageRetrievedFromDb.get(0).getCritiquePresse().toString());
		assertEquals(ALLOCINE_FIULM_ID_136316,allFicheFilmFromPageRetrievedFromDb.get(1).getAllocineFilmId());
		assertEquals(ALLOCINE_FIULM_ID_136316_TITLE,allFicheFilmFromPageRetrievedFromDb.get(1).getTitle());
		Optional<FicheFilm> optionalFicheFilmRetrievedFromDb = allocineService.retrieveFicheFilm(allFicheFilmFromPageRetrievedFromDb.get(0).getId());
		FicheFilm ficheFilmRetrievedFromDb = allocineService.retrieveFicheFilmByTitle(allFicheFilmFromPageRetrievedFromDb.get(0).getTitle());
		assertTrue(optionalFicheFilmRetrievedFromDb.isPresent());
		assertEquals(allFicheFilmFromPageRetrievedFromDb.get(0),optionalFicheFilmRetrievedFromDb.get());
		assertEquals(allFicheFilmFromPageRetrievedFromDb.get(0).getTitle(),optionalFicheFilmRetrievedFromDb.get().getTitle());
		assertEquals(allFicheFilmFromPageRetrievedFromDb.get(0),ficheFilmRetrievedFromDb);
    }
}
