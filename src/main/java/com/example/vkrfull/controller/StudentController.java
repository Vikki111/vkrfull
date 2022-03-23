package com.example.vkrfull.controller;

import com.example.vkrfull.model.Graph;
import com.example.vkrfull.model.Student;
import com.example.vkrfull.model.StudentFilterBody;
import com.example.vkrfull.service.ExerciseServiceImpl;
import com.example.vkrfull.service.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                System.out.println("RESPONSE: "+s);
                exerciseService.parsePythonResponse(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println("ERROR: " +s);
            }

        }
        catch (IOException e) {
            System.out.println("exception happened ");
            e.printStackTrace();
        }


        String result = exerciseService.validate(pythonGraph, student.getGraph(), student.getExerciseId()).toString();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/students/graph/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String getGraph(@PathVariable(name = "id") int id) {
        log.info("student id '{}'", id);
        String result = studentService.get(id).getGraph();
        log.info("student graph:  {}", result);
        return result;
    }

    @PostMapping(value = "/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Student student) {
        log.debug("student '{}'", student);
        Student savedStudent = studentService.create(student);
        log.info("new entity is created");
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
        log.info("get entity");
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping(value = "/students/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Student> get(@PathVariable(name = "id") int id) {
        log.debug("id '{}'", id);
        final Student student = studentService.get(id);
        log.debug("student '{}'", student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PutMapping(value = "/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id,
                                    @RequestBody Student student) {
        log.debug("studentBody '{}'", student);
        studentService.update(student, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        studentService.delete(id);
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
