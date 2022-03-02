package com.example.vkrfull.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentFilterBody {
    private Integer id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String department;
    private Integer mark;
    private String graph;
    private Boolean ready;
    private String comment;
    private Integer exerciseId;
}
