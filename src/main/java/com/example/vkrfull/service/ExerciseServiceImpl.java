package com.example.vkrfull.service;

import com.example.vkrfull.model.Edge;
import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.model.Graph;
import com.example.vkrfull.repository.ExerciseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExerciseServiceImpl {

    private final ExerciseRepository exerciseRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public Graph parsePythonResponse(String str) {
        str = str.replaceAll("[({')]", "");
        str = str.replaceAll("}", "/");
        str = str.substring(0, str.length()-2);
        System.out.println(str);
        String[] strings = str.split("/, ");
        Graph graph = new Graph();
        graph.setEdges(new ArrayList<>());
        for (String str1: strings) {
            String[] leftright = str1.split(": ");
            String[] left = leftright[0].split(", ");
            String[] right = leftright[1].split(", ");
            String source = left[0];
            String target = left[1];
            for (String label : right) {
                graph.getEdges().add(new Edge(source, target, label));
            }
        }
        System.out.println(graph.getEdges());
        return graph;
    }

    public Boolean validate(Graph pythonGraph, String graphJson, Integer exerciseId) {
        log.debug("graph '{}' and exerciseId '{}'", graphJson, exerciseId);
        ObjectMapper objectMapper = new ObjectMapper();
        Graph graph = new Graph();
        try {
            graph = objectMapper.readValue(graphJson, Graph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("python graph"+ pythonGraph);
        System.out.println("stud graph " + graph);
        return true;
    }

    public void create(Exercise exercise) {
        exerciseRepository.save(exercise);
    }

    public void update(Exercise newExercise, int id) {
        if (exerciseRepository.existsById(id)) {
            newExercise.setId(id);
            exerciseRepository.save(newExercise);
        }
    }

    public Exercise get(int id) {
        return exerciseRepository.getById(id);
    }

    public List<Exercise> getAll() {
        return exerciseRepository.findAll();
    }

    public void delete(int id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
        }
    }

}
