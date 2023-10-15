package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LogManager.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound()
                .build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("Start create user...");
        User user = new User();
        user.setUsername(createUserRequest.getUsername());

        log.info("User name set with " + createUserRequest.getUsername());

        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        if (!createUserRequest.getPassword().isEmpty()) {
            if (createUserRequest.getPassword()
                    .length() < 7 || !createUserRequest.getPassword()
                    .equals(createUserRequest.getConfirmPassword())) {
                log.warn("User password invalid!");
            }
        } else {
            log.warn("User password is empty!");
            throw new RuntimeException("Password is null");
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

        userRepository.save(user);

        log.info("Create user success!");

        return ResponseEntity.ok(user);
    }

}
