package fr.fredos.dvdtheque.rest.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.fredos.dvdtheque.integration.config.ContextConfiguration;
import fr.fredos.dvdtheque.integration.config.HazelcastConfiguration;
import fr.fredos.dvdtheque.rest.model.ExcelFilmHandler;
import fr.fredos.dvdtheque.rest.service.IFilmService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HazelcastConfiguration.class, ContextConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FilmControllerForImportFilmListTest {
	protected Logger logger = LoggerFactory.getLogger(FilmControllerForImportFilmListTest.class);
	private static final String 					GET_ALL_FILMS_URI = "/dvdtheque-service/films/";
	@Autowired
	protected IFilmService 							filmService;
	@Autowired
	ExcelFilmHandler 								excelFilmHandler;

    
    @Autowired
    private Environment 							environment;
    
	private static final String 					IMPORT_FILM_LIST_URI = GET_ALL_FILMS_URI + "import";
	private static final String 					contentType = "text/plain";
	@Autowired
	private MockMvc 								mockmvc;
	@MockBean
	private JwtDecoder 								jwtDecoder;

	@Test
	@Disabled
	public void testImportFilmListFromCsv() throws Exception {
		Resource resource = new ClassPathResource("ListeDVD.csv");
		File file = resource.getFile();
		byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "ListDvd.csv", contentType, content);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(IMPORT_FILM_LIST_URI)
				.file(mockMultipartFile);
		
		mockmvc.perform(MockMvcRequestBuilders.get(environment.getRequiredProperty(FilmController.DVDTHEQUE_BATCH_SERVICE_URL)
				+ environment.getRequiredProperty(FilmController.DVDTHEQUE_BATCH_SERVICE_IMPORT)))
		.andExpect(status().isOk());
		
		mockmvc.perform(builder.with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
	}
	
	@Test
	@Disabled
	public void testImportFilmListFromExcel() throws Exception {
		Resource resource = new ClassPathResource("ListeDVD.xlsx");
		File file = resource.getFile();
		byte[] bFile = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file.getName(), contentType, bFile);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(IMPORT_FILM_LIST_URI)
				.file(mockMultipartFile);
		mockmvc.perform(MockMvcRequestBuilders.get(environment.getRequiredProperty(FilmController.DVDTHEQUE_BATCH_SERVICE_URL)
				+ environment.getRequiredProperty(FilmController.DVDTHEQUE_BATCH_SERVICE_IMPORT)).with(jwt().jwt(build -> build.subject("test")))
				.with(csrf()))
		.andExpect(status().isOk());
		mockmvc.perform(builder)
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
	}
}
