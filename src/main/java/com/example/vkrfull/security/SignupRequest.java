package com.example.vkrfull.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupRequest {

    private String username;

    private Set<String> role;

    private String password;

    private Integer student;

}
