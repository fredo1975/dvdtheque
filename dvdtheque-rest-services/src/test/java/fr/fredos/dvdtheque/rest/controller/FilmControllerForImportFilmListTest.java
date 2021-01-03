package fr.fredos.dvdtheque.rest.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerForImportFilmListTest {
	protected Logger logger = LoggerFactory.getLogger(FilmControllerTest.class);
	private MockMvc mvc;
	@Autowired
	protected IFilmService filmService;
	@Autowired
	ExcelFilmHandler excelFilmHandler;
	@Autowired
    private WebApplicationContext context;
	private static final String contentType = "text/plain";
	private static final String BASE_PATH_URI = "/dvdtheque";
	@Before()
	public void setUp() throws Exception {
		filmService.cleanAllFilms();
		mvc = MockMvcBuilders
		          .webAppContextSetup(context)
		          //.apply(springSecurity())
		          .build();
	}
	
	@Test
	public void testImportFilmListFromCsv() throws Exception {
		Resource resource = new ClassPathResource("ListeDVD.csv");
		File file = resource.getFile();
		byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "ListDvd.csv", contentType, content);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(BASE_PATH_URI+FilmController.IMPORT_PATH)
				.file(mockMultipartFile);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
	}
	
	@Test
	public void testImportFilmListFromExcel() throws Exception {
		Resource resource = new ClassPathResource("ListeDVD.xlsx");
		File file = resource.getFile();
		byte[] bFile = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file.getName(), contentType, bFile);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(BASE_PATH_URI+FilmController.IMPORT_PATH)
				.file(mockMultipartFile);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
	}
}
