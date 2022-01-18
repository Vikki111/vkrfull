package com.example.vkrfull.controller;

import com.example.vkrfull.model.Student;
import com.example.vkrfull.service.ExerciseServiceImpl;
import com.example.vkrfull.service.StudentServiceImpl;
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
public class StudentController {

    private final StudentServiceImpl studentService;
    private final ExerciseServiceImpl exerciseService;

    @Autowired
    public StudentController(StudentServiceImpl studentService, ExerciseServiceImpl exerciseService) {
        this.studentService = studentService;
        this.exerciseService = exerciseService;
    }

    @PostMapping(value = "/students/check")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> check(@RequestBody Student student) {
        log.debug("studentBody '{}'", student);
        exerciseService.check(student.getGraph(), student.getExerciseId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/students")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Student student) {
        log.debug("studentBody '{}'", student);
        studentService.create(student);
        log.info("new entity is created");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/students")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Student>> getAll() {
        final List<Student> students = studentService.getAll();
        log.info("get entity");
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping(value = "/students/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Student> get(@PathVariable(name = "id") int id) {
        log.debug("id '{}'", id);
        final Student student = studentService.get(id);
        log.debug("student '{}'", student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PutMapping(value = "/students/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id,
                                    @RequestBody Student student) {
        log.debug("studentBody '{}'", student);
        studentService.update(student, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/students/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        studentService.delete(id);
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
