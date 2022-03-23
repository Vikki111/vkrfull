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
import java.util.Arrays;
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
        return graph;
    }

    public Boolean validate(Graph pythonGraph, String graphJson, Integer exerciseId) {
        log.debug("graph '{}' and exerciseId '{}'", graphJson, exerciseId);
        ObjectMapper objectMapper = new ObjectMapper();
        Graph studGraph = new Graph();
        try {
            studGraph = objectMapper.readValue(graphJson, Graph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        parseStudentGraph(studGraph);
        System.out.println("python graph"+ pythonGraph);
        System.out.println("stud graph " + studGraph);
        return true;
    }

    public void parseStudentGraph(Graph studGraph) {
        List<Edge> newEdges = new ArrayList<>(studGraph.getEdges());
        for (Edge edge : studGraph.getEdges()) {
            String str = edge.getLabel();
            if (edge.getLabel().contains("space")) {
                newEdges.remove(edge);
                newEdges.add(new Edge(edge.getSource(), edge.getTarget(), " "));
            }
            if (edge.getLabel().contains("A|...|Z")) {
                str = str.replaceAll("A\\|...\\|Z", "");
                alpha(newEdges, edge);
            }
            if (edge.getLabel().contains("1|...|9")) {
                str = str.replaceAll("1\\|...\\|9", "");
                digit(newEdges, edge);
            }
            if (edge.getLabel().contains("0|...|9")) {
                str = str.replaceAll("0\\|...\\|9", "");
                digitzero(newEdges, edge);
            }
            if (str.contains("|")) {
                newEdges.remove(edge);
                String[] strings = str.split("\\|");
                for (String symbol : strings) {
                    if (!symbol.equals("")) {
                        newEdges.add(new Edge(edge.getSource(), edge.getTarget(), symbol));
                    }
                }
            }
        }
        studGraph.getEdges().clear();
        studGraph.getEdges().addAll(newEdges);
    }

    public void alpha(List<Edge> newEdges, Edge edge) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 65; i < 91; i++) {
            newEdges.add(new Edge(edge.getSource(), edge.getTarget(), String.valueOf((char)i)));
        }
    }

    public void digit(List<Edge> newEdges, Edge edge) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 1; i < 10; i++) {
            newEdges.add(new Edge(edge.getSource(), edge.getTarget(), String.valueOf(i)));
        }
    }

    public void digitzero(List<Edge> newEdges, Edge edge) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 0; i < 10; i++) {
            newEdges.add(new Edge(edge.getSource(), edge.getTarget(), String.valueOf(i)));
        }
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
