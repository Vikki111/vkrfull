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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@Slf4j
public class GraphController {

    private final StudentServiceImpl studentService;
    private final ExerciseServiceImpl exerciseService;

    @Autowired
    public GraphController(StudentServiceImpl studentService, ExerciseServiceImpl exerciseService) {
        this.studentService = studentService;
        this.exerciseService = exerciseService;
    }

//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = { "/savejson" }, method = RequestMethod.POST)
    public String savejson(@RequestHeader(value = "student-id") UUID studentId,
                            @RequestBody String graph) {
        System.out.println(studentId);
        Student student = studentService.get(studentId);
        student.setGraph(graph);
        studentService.update(student, studentId);
        return graph;
    }

    @GetMapping(value = "/graph/validate/{studentId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> validateGraph(
                                    @PathVariable(name = "studentId")
                                            String studentId) {
        System.out.println("StudentId "+ studentId);
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        String result = String.join(",", strings);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
