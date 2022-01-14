package com.example.eComPractice.controllers;

import com.example.eComPractice.TestUtils;
import com.example.eComPractice.model.persistence.User;
import com.example.eComPractice.model.persistence.repositories.CartRepository;
import com.example.eComPractice.model.persistence.repositories.UserRepository;
import com.example.eComPractice.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testpassword")).thenReturn("testpassword");

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testpassword");
        r.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("testpassword", u.getPassword());

    }

    @Test
    public void create_user_wrong_path() throws Exception {
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testpassword");
        r.setConfirmPassword("testpassword1");

        final ResponseEntity<User> response = userController.createUser(r);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findUserByNameHappyPath() {
        User user = new User();
        user.setUsername("test");
        user.setId(1L);

        when (userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(user.getUsername());

        assertEquals(200, response.getStatusCodeValue());

        User foundUser = response.getBody();
        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
    }

    public void findUserByNameFailure() {
        ResponseEntity<User> response = userController.findByUserName("ttttt");
        assertEquals(404, response.getStatusCodeValue());
    }
}
