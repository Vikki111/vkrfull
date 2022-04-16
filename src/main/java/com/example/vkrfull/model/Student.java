package com.example.vkrfull.model;

//Create table student
//        (
//        id SERIAL PRIMARY KEY,
//        last_name varchar(40),
//        first_name varchar(40),
//        patronymic varchar(40),
//        mark integer,
//        graph varchar(50000),
//        ready boolean,
//        comment varchar(5000),
//        exercise_id integer,
//        FOREIGN KEY (exercise_id) REFERENCES exercise (id) ON DELETE RESTRICT ON UPDATE CASCADE
//        );

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})//////////////
public class Student {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "department")
    private String department;
    @Column(name = "mark")
    private Integer mark;
    @Column(name = "graph")
    private String graph;
    @Column(name = "ready")
    private Boolean ready;
    @Column(name = "comment")
    private String comment;
    @Column(name = "exercise_id")
    private UUID exerciseId;

    @Transient
    private Integer exerciseNumber;
}
