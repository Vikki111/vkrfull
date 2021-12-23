package com.example.vkrfull;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class VkrfullApplication {

	public static void main(String[] args) {
		SpringApplication.run(VkrfullApplication.class, args);
	}

}
