package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser_OK() {
        when(encoder.encode("testPass")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPass");
        request.setConfirmPassword("testPass");

        ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void testFindById_OK() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPass");
        request.setConfirmPassword("testPass");

        ResponseEntity<User> uR = userController.createUser(request);
        userController.createUser(request);
        assertNotNull(uR);

        User u = uR.getBody();
        assertNotNull(u);

        when(userRepo.findById(u.getId())).thenReturn(Optional.of(u));

        ResponseEntity<User> response = userController.findById(u.getId());
        assertNotNull(response);
        User findUser = response.getBody();

        assertNotNull(findUser);
        assertEquals(u.getId(), findUser.getId());
        assertEquals(u.getUsername(), findUser.getUsername());
    }

    @Test
    public void testFindByUsername_OK() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPass");
        request.setConfirmPassword("testPass");

        ResponseEntity<User> uR = userController.createUser(request);
        userController.createUser(request);
        assertNotNull(uR);

        User u = uR.getBody();
        assertNotNull(u);

        when(userRepo.findByUsername(u.getUsername())).thenReturn(u);

        ResponseEntity<User> response = userController.findByUserName(u.getUsername());
        assertNotNull(response);
        User findUser = response.getBody();

        assertNotNull(findUser);
        assertEquals(u.getId(), findUser.getId());
        assertEquals(u.getUsername(), findUser.getUsername());
    }

    @Test
    public void testFindByUsername_NOT_OK() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPass");
        request.setConfirmPassword("testPass");

        ResponseEntity<User> uR = userController.createUser(request);
        userController.createUser(request);
        assertNotNull(uR);

        User u = uR.getBody();
        assertNotNull(u);

        when(userRepo.findByUsername(u.getUsername())).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName(u.getUsername());
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
    }

    private User createTestUser() {
        User u = new User();
        u.setUsername("test username");
        u.setPassword("test password");
        u.setId(0);
        return u;
    }
}
