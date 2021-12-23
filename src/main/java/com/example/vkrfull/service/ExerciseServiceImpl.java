package com.example.vkrfull.service;

import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseServiceImpl {

    private final ExerciseRepository exerciseRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
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
