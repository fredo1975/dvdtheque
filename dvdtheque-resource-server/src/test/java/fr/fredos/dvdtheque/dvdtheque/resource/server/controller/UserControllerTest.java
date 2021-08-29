package fr.fredos.dvdtheque.dvdtheque.resource.server.controller;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.hazelcast.core.Hazelcast;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserController.class,SpringSecurityWebAuxTestConfig.class})
@AutoConfigureMockMvc
@WebAppConfiguration
@ActiveProfiles("local")
public class UserControllerTest {
	protected Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
	private MockMvc mockMvc;
	@Autowired
    private WebApplicationContext context;
	
	@BeforeEach()
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
		          .webAppContextSetup(context)
		          .apply(SecurityMockMvcConfigurers.springSecurity())
		          .build();
	}
	
	@After
	public void after() {
	    Hazelcast.shutdownAll();
	}

	@Test
	//@WithUserDetails("fredo")
	@WithMockUser(username="fredo",roles = {"USER2"})
	public void getTest() throws Exception {
		//SecurityContext context = SecurityContextHolder.getContext();
		mockMvc.perform(MockMvcRequestBuilders.get("/salute")
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
