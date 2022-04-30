package com.example.vkrfull.controller;

import com.example.vkrfull.model.Graph;
import com.example.vkrfull.model.Student;
import com.example.vkrfull.service.ExerciseServiceImpl;
import com.example.vkrfull.service.StudentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.springframework.util.CollectionUtils.isEmpty;

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
//        List<String> strings = new ArrayList<>();
//        strings.add("1");
//        strings.add("2");
//        strings.add("3");
//        String result = String.join(",", strings);

        Student student = studentService.get(UUID.fromString(studentId));
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
        } catch (IOException e) {
            System.out.println("exception happened ");
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Graph studGraph = new Graph();
        try {
            studGraph = objectMapper.readValue(student.getGraph(), Graph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String responseParseStudGraph = exerciseService.parseStudentGraph(studGraph);
        HashMap<String, String> equal = new HashMap<String, String>();
        boolean flag = isEquiv(equal, studGraph, "1", pythonGraph, "1");
        System.out.printf("Result %b%n", flag);
        for (Map.Entry entry: equal.entrySet()) {
            System.out.printf("%s %s%n", entry.getKey(), entry.getValue());
        }
//        String result = "1,2,3";
        String result = String.join(",", equal.keySet());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private static boolean checkNode(HashMap<String, String> equal, HashMap<String, String> checked, Graph grStd, String nodeStd, Graph grChk, String nodeChk) {
        // Если вершина уже была проверена ранее
        if (checked.keySet().size() > 0 && checked.get(nodeStd) !=null && checked.get(nodeStd).equals(nodeChk)){
            return true;
        } else {
            checked.put(nodeStd, nodeChk);
            // Получаем символы перехода и связанные дочерние вершины
            HashMap<String, String> edgesStd = grStd.getEdges(nodeStd);
            HashMap<String, String> edgesChk = grChk.getEdges(nodeChk);

            // Проверяем состав символов перехода, он должен быть одинаков
            Set<String> setStd = edgesStd.keySet();
            Set<String> setChk = edgesChk.keySet();
            if (edgesStd.keySet().equals(edgesChk.keySet())){
                equal.put(nodeStd, nodeChk);
                boolean flag = true;
                // Перебираем все ребра у текущей вершины в эталонном графе
                for (Map.Entry entryStd : edgesStd.entrySet()) {
                    // Проверяем есть ли ребра с такой пометкой в проверяемом графе
                    if (edgesChk.containsKey(entryStd.getKey())){
                        // Проверяем рекурсивно на эквивалентность
                        flag = flag && checkNode(equal, checked, grStd, entryStd.getValue().toString(), grChk, edgesChk.get(entryStd.getKey()));
                    }
                }
                if (!flag){
                    checked.remove(nodeStd);
                }
                return flag;
            } else {
                checked.remove(nodeStd);
                return false;
            }
        }
    }

    // Проверяет два графа на эквивалентность
    // Предпологается, что графы связанные и детерминированные
    public static boolean isEquiv(HashMap<String, String> equal, Graph grStd, String startStd, Graph grChk, String startChk) {
        HashMap<String, String> checked = new HashMap<String, String>();
        return checkNode(equal, checked, grStd, startStd, grChk, startChk);
    }
}
