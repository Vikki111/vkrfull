package com.example.vkrfull.controller;

import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.service.ExerciseServiceImpl;
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
public class ExerciseController {

    private final ExerciseServiceImpl exerciseService;

    @Autowired
    public ExerciseController(ExerciseServiceImpl exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping(value = "/exercises")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Exercise exercise) {
        log.debug("exerciseBody '{}'", exercise);
        exerciseService.create(exercise);
        log.info("new entity is created");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/exercises")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Exercise>> getAll() {
        final List<Exercise> exercises = exerciseService.getAll();
        log.info("get entity");
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }

    @GetMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Exercise> get(@PathVariable(name = "id") int id) {
        log.debug("id '{}'", id);
        final Exercise exercise = exerciseService.get(id);
        log.debug("exercise '{}'", exercise);
        return new ResponseEntity<>(exercise, HttpStatus.OK);
    }

    @PutMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id,
                                    @RequestBody Exercise exercise) {
        log.debug("exerciseBody '{}'", exercise);
        exerciseService.update(exercise, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        exerciseService.delete(id);
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
