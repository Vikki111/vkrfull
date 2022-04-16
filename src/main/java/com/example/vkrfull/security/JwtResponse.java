package com.example.vkrfull.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String username;
    private List<String> roles;
    private UUID student;

    public JwtResponse(String accessToken, UUID id, String username, List<String> roles, UUID student) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.student = student;
    }

}
