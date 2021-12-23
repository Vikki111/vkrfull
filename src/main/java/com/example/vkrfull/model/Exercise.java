package com.example.vkrfull.model;

//Create table exercise
//        (
//        id SERIAL PRIMARY KEY,
//        exercise varchar(5000),
//        description varchar(5000),
//        graph varchar(50000)
//        );

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exercise")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "exercise")
    private String exercise;
    @Column(name = "description")
    private String description;
    @Column(name = "graph")
    private String graph;
}
