package com.example.vkrfull.repository;

import com.example.vkrfull.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
