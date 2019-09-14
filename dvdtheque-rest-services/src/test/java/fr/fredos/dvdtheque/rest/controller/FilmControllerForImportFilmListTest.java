package fr.fredos.dvdtheque.rest.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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

import fr.fredos.dvdtheque.service.IFilmService;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerForImportFilmListTest {
	protected Logger logger = LoggerFactory.getLogger(FilmControllerTest.class);
	@Autowired
	private MockMvc mvc;
	private static final String GET_ALL_FILMS_URI = "/dvdtheque/films/";
	@Autowired
	protected IFilmService filmService;
	@Autowired
	ExcelFilmHandler excelFilmHandler;
	private static final String IMPORT_FILM_LIST_URI = GET_ALL_FILMS_URI + "import";

	@Test
	public void testImportFilmList() throws Exception {
		Resource resource = new ClassPathResource("ListeDVD.csv");
		File file = resource.getFile();
		StringBuilder sb = new StringBuilder();
		try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line+"\n");
			}
		}
		byte[] content = sb.toString().getBytes();
		String contentType = "text/plain";
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "ListDvd.csv", contentType, content);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(IMPORT_FILM_LIST_URI)
				.file(mockMultipartFile);
		mvc.perform(builder).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

	}
}
