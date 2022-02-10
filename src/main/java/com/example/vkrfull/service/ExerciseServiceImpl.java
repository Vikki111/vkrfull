package com.example.vkrfull.service;

import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.model.Graph;
import com.example.vkrfull.repository.ExerciseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ExerciseServiceImpl {

    private final ExerciseRepository exerciseRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }


    public Boolean validate(String graphJson, Integer exerciseId) {
        log.debug("graph '{}' and exerciseId '{}'", graphJson, exerciseId);
        ObjectMapper objectMapper = new ObjectMapper();
        Graph graph = new Graph();
        try {
            graph = objectMapper.readValue(graphJson, Graph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(graph);
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
