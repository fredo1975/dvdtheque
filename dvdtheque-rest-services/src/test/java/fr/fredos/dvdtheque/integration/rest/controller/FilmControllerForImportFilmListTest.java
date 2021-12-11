package fr.fredos.dvdtheque.integration.rest.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.c4_soft.springaddons.security.oauth2.test.mockmvc.MockMvcSupport;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.keycloak.ServletKeycloakAuthUnitTestingSupport;

import fr.fredos.dvdtheque.batch.BatchApplication;
import fr.fredos.dvdtheque.rest.controller.DvdthequeRestApplication;
import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={DvdthequeRestApplication.class,fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.tmdb.service.TmdbServiceApplication.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		BatchApplication.class},webEnvironment = WebEnvironment.RANDOM_PORT,
				properties = { "eureka.client.enabled:false", "spring.cloud.config.enabled:false" })
@AutoConfigureMockMvc
@Import({ ServletKeycloakAuthUnitTestingSupport.class})
@ActiveProfiles("test")
public class FilmControllerForImportFilmListTest {
	protected Logger logger = LoggerFactory.getLogger(FilmControllerForImportFilmListTest.class);
	private static final String 					GET_ALL_FILMS_URI = "/dvdtheque/films/";
	@Autowired
	protected IFilmService 							filmService;
	@Autowired
	ExcelFilmHandler 								excelFilmHandler;
	@Autowired
	private ServletKeycloakAuthUnitTestingSupport 	keycloak;
	@Autowired
	private MockMvcSupport 							api;
	private static final String 					IMPORT_FILM_LIST_URI = GET_ALL_FILMS_URI + "import";
	private static final String 					contentType = "text/plain";
	
	@Test
	public void testImportFilmListFromCsv() throws Exception {
		Resource resource = new ClassPathResource("ListeDVD.csv");
		File file = resource.getFile();
		byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "ListDvd.csv", contentType, content);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(IMPORT_FILM_LIST_URI)
				.file(mockMultipartFile);
		api
		.with(keycloak.keycloakAuthenticationToken().roles("user").accessToken(token -> token.setPreferredUsername("fredo")))
		.perform(builder)
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
	}
	
	@Test
	public void testImportFilmListFromExcel() throws Exception {
		Resource resource = new ClassPathResource("ListeDVD.xlsx");
		File file = resource.getFile();
		byte[] bFile = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file.getName(), contentType, bFile);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(IMPORT_FILM_LIST_URI)
				.file(mockMultipartFile);
		
		api
		.with(keycloak.keycloakAuthenticationToken().roles("user").accessToken(token -> token.setPreferredUsername("fredo")))
		.perform(builder)
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
	}
}
