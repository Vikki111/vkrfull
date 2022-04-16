package com.example.vkrfull.controller;

import com.example.vkrfull.model.Student;
import com.example.vkrfull.security.User;
import com.example.vkrfull.security.UserRepository;
import com.example.vkrfull.service.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@RestController
@CrossOrigin
public class UserController {
    private final UserRepository userRepository;
    private final StudentServiceImpl studentService;

    @Autowired
    public UserController(UserRepository userRepository, StudentServiceImpl studentService) {
        this.userRepository = userRepository;
        this.studentService = studentService;
    }

    @GetMapping(value = "/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        final List<User> users = userRepository.findAll();
        for (User user : users) {
            if (nonNull(user.getStudent())) {
                Student student = studentService.get(user.getStudent());
                user.setLastName(student.getLastName());
                user.setFirstName(student.getFirstName());
                user.setDepartment(student.getDepartment());
            }
        }
        log.info("get entity");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") UUID id) {
        User user = userRepository.findById(id).get();
        studentService.delete(user.getStudent());
        userRepository.delete(user);
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
