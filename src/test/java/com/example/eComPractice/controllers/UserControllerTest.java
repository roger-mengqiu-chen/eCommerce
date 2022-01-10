package com.example.eComPractice.controllers;

import com.example.eComPractice.TestUtils;
import com.example.eComPractice.model.persistence.User;
import com.example.eComPractice.model.persistence.repositories.CartRepository;
import com.example.eComPractice.model.persistence.repositories.UserRepository;
import com.example.eComPractice.model.requests.CreateUserRequest;
import com.example.eComPractice.model.requests.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
//
//    private CartRepository cartRepository = mock(CartRepository.class);
//
//    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
       // when(encoder.encode("testpassword")).thenReturn("testpassword");

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testpassword");
        r.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(1, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("testpassword", u.getPassword());

    }
    @Test
    public void loginTest() throws Exception{
       // when(encoder.encode("password")).thenReturn("password");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("password");
        r.setConfirmPassword("password");
        userController.createUser(r);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("password");
        String json = new ObjectMapper().writeValueAsString(loginRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());
    }
}
