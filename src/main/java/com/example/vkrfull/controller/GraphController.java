package com.example.vkrfull.controller;

import com.example.vkrfull.model.Student;
import com.example.vkrfull.service.ExerciseServiceImpl;
import com.example.vkrfull.service.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = { "/savejson" }, method = RequestMethod.POST)
    public String savejson(@RequestHeader(value = "student-id") int studentId,
                            @RequestBody String graph) {
        System.out.println(studentId);
        Student student = studentService.get(studentId);
        student.setGraph(graph);
        studentService.update(student, studentId);
        return graph;
    }
}