package fr.fredos.dvdtheque.dvdtheque.authorization.server.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.jwt.payload.LoginRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class AuthenticationControllerTest {
	protected Logger logger = LoggerFactory.getLogger(AuthenticationControllerTest.class);
	private MockMvc mvc;
	@Autowired
    private WebApplicationContext context;
	@Autowired
	private ObjectMapper mapper;
	
	private final static String BASE_PATH = "/api/auth/";
	@Before()
	public void setUp() throws Exception {
		mvc = MockMvcBuilders
		          .webAppContextSetup(context)
		          .apply(SecurityMockMvcConfigurers.springSecurity())
		          .build();
	}
	
	@Test
	public void signin() throws Exception {
		final LoginRequest req = new LoginRequest();
		req.setUsername("fredo");
		req.setPassword("fredo");
		String loginRequestJsonString = mapper.writeValueAsString(req);
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BASE_PATH+AuthenticationController.SIGNIN_PATH)
				.contentType(MediaType.APPLICATION_JSON).content(loginRequestJsonString)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
		assertNotNull(resultActions.andReturn().getResponse().getContentAsString());
	}
}
