package com.example.vkrfull.model;

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
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "description")
    private String description;
    @Column(name = "graph")
    private String graph;
}
