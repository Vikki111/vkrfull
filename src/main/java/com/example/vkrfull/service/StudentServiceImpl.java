package com.example.vkrfull.service;

import com.example.vkrfull.model.Student;
import com.example.vkrfull.model.StudentFilterBody;
import com.example.vkrfull.repository.StudentRepository;
import com.example.vkrfull.repository.StudentRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentServiceImpl {
    
    private final StudentRepository studentRepository;
    private final StudentRepositoryImpl studentRepositoryImpl;

    public StudentServiceImpl(StudentRepository studentRepository, StudentRepositoryImpl studentRepositoryImpl) {
        this.studentRepository = studentRepository;
        this.studentRepositoryImpl = studentRepositoryImpl;
    }

    public Student create(Student student) {
        return studentRepository.save(student);
    }

    public void update(Student newStudent, UUID id) {
        if (studentRepository.existsById(id)) {
            newStudent.setId(id);
            studentRepository.save(newStudent);
        }
    }

    public Student get(UUID id) {
        return studentRepository.getById(id);
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public List<Student> find(StudentFilterBody studentFilterBody) {
        return  studentRepositoryImpl.find(studentFilterBody);
    }

    public void delete(UUID id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        }
    }
}
