package com.example.vkrfull.service;

import com.example.vkrfull.model.Student;
import com.example.vkrfull.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl {
    
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student create(Student student) {
        return studentRepository.save(student);
    }

    public void update(Student newStudent, int id) {
        if (studentRepository.existsById(id)) {
            newStudent.setId(id);
            studentRepository.save(newStudent);
        }
    }

    public Student get(int id) {
        return studentRepository.getById(id);
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public void delete(int id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        }
    }
}
