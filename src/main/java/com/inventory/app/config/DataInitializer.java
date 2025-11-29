package com.inventory.app.config;

import com.inventory.app.model.User;
import com.inventory.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if it doesn't exist
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                Set<String> roles = new HashSet<>();
                roles.add("ADMIN");
                admin.setRoles(roles);
                userRepository.save(admin);
                System.out.println("Created default admin user (username: admin, password: admin123)");
            }
        };
    }
}
