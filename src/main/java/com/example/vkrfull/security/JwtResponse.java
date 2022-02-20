package com.example.vkrfull.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private List<String> roles;
    private Long student;

    public JwtResponse(String accessToken, Long id, String username, List<String> roles, Long student) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.student = student;
    }

}
