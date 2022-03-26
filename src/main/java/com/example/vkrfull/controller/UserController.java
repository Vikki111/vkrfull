package com.example.vkrfull.controller;

import com.example.vkrfull.security.User;
import com.example.vkrfull.security.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        final List<User> users = userRepository.findAll();
        log.info("get entity");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        userRepository.delete(userRepository.findById(id).get());
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
