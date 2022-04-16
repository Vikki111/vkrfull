package com.example.vkrfull.config;


import com.example.vkrfull.repository.ExerciseRepository;
import com.example.vkrfull.repository.StudentRepository;
import com.example.vkrfull.repository.StudentRepositoryImpl;
import com.example.vkrfull.security.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
class AppInitializator {

    @Autowired
    PasswordEncoder encoder;

    private final ExerciseRepository exerciseRepository;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StudentRepositoryImpl studentRepositoryImpl;

    public AppInitializator(ExerciseRepository exerciseRepository,
                            StudentRepository studentRepository,
                            StudentRepositoryImpl studentRepositoryImpl,
                            UserRepository userRepository,
                            RoleRepository roleRepository) {
        this.exerciseRepository = exerciseRepository;
        this.studentRepository = studentRepository;
        this.studentRepositoryImpl = studentRepositoryImpl;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    private void init() {
        log.info("AppInitializator initialization logic ...");
        try {
            roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } catch (Exception e) {
            roleRepository.save(new Role(UUID.randomUUID(), ERole.ROLE_ADMIN));
        }
        try {
            roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } catch (Exception e) {
            roleRepository.save(new Role(UUID.randomUUID(), ERole.ROLE_USER));
        }
        if (!userRepository.existsByUsername("admin")) {
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            User user = new User("admin",
                    encoder.encode("admin"));
            user.setRoles(roles);
            user.setStudent(null);
            user.setId(UUID.randomUUID());
            userRepository.save(user);
        }
    }
}
