package com.example.vkrfull.controller;

import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.model.Graph;
import com.example.vkrfull.model.Student;
import com.example.vkrfull.model.StudentFilterBody;
import com.example.vkrfull.security.User;
import com.example.vkrfull.security.UserRepository;
import com.example.vkrfull.service.ExerciseServiceImpl;
import com.example.vkrfull.service.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin
public class StudentController {

    private final StudentServiceImpl studentService;
    private final ExerciseServiceImpl exerciseService;
    private final UserRepository userRepository;

    @Autowired
    public StudentController(StudentServiceImpl studentService,
                             ExerciseServiceImpl exerciseService,
                             UserRepository userRepository) {
        this.studentService = studentService;
        this.exerciseService = exerciseService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/students/validate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> validate(@RequestBody Student student) {
        log.debug("studentBody '{}'", student);
        String ex = exerciseService.get(student.getExerciseId()).getExercise();
        ex = ex.replaceAll(" \\\\n", "///");
        ex = ex.replaceAll(" ", "/");
        Graph pythonGraph = new Graph();
        try {
            String s = null;
            Process p = Runtime.getRuntime().exec("python src/main/java/com/example/vkrfull/controller/maintestjava.py "+ex);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                System.out.println("RESPONSE: "+s);
                pythonGraph = exerciseService.parsePythonResponse(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println("ERROR: " +s);
            }
        }
        catch (IOException e) {
            System.out.println("exception happened ");
            e.printStackTrace();
        }
        String result = exerciseService.validate(pythonGraph, student.getGraph(), student.getExerciseId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/students/graph/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String getGraph(@PathVariable(name = "id") UUID id) {
        log.info("student id '{}'", id);
        String result = studentService.get(id).getGraph();
        log.info("student graph:  {}", result);
        return result;
    }

    @PostMapping(value = "/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Student student) {
        log.info("student '{}'", student);
        student.setId(UUID.randomUUID());
        Student savedStudent = studentService.create(student);
        log.info("new student is created with id: {}", student.getId());
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @PostMapping(value = "/students/find")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> find(@RequestBody StudentFilterBody studentFilterBody) {
        log.info("studentBody '{}'", studentFilterBody);
        List<Student> students = studentService.find(studentFilterBody);
        log.info("Students found: {}", students);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping(value = "/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Student>> getAll() {
        final List<Student> students = studentService.getAll();
        final List<Exercise> exercises = exerciseService.getAll();
        Map<UUID, Exercise> map = exercises.stream()
                .collect(Collectors.toMap(Exercise::getId, Function.identity()));
        for (Student student : students) {
            student.setExerciseNumber(map.get(student.getExerciseId()).getNumber());
        }
        log.info("get entity");
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping(value = "/students/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Student> get(@PathVariable(name = "id") UUID id) {
        log.debug("id '{}'", id);
        final Student student = studentService.get(id);
        Exercise exercise = exerciseService.get(student.getExerciseId());
        student.setExerciseNumber(exercise.getNumber());
        log.debug("student '{}'", student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PutMapping(value = "/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable(name = "id") UUID id,
                                    @RequestBody Student student) {
        log.debug("studentBody '{}'", student);
        studentService.update(student, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") UUID id) {
        User user = userRepository.findByStudent(id).get();
        userRepository.delete(user);
        studentService.delete(id);
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
