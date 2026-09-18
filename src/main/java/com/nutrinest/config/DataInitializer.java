package com.nutrinest.config;

import com.nutrinest.entity.Role;
import com.nutrinest.entity.User;
import com.nutrinest.repository.RoleRepository;
import com.nutrinest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // =====================================================
        // CREATE ADMIN ROLE IF NOT EXISTS
        // =====================================================

        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName("ADMIN");
                    return roleRepository.save(role);
                });


        // =====================================================
        // CREATE DEFAULT ADMIN IF NOT EXISTS
        // =====================================================

        if (userRepository.findByEmail("admin@nutrinest.com").isEmpty()) {

            User admin = new User();

            admin.setFullName("Administrator");
            admin.setEmail("admin@nutrinest.com");

            // Password is encoded using BCrypt
            admin.setPassword(
                    passwordEncoder.encode("admin123")
            );

            // Phone is required by the database
            admin.setPhone("9999999999");

            admin.setRole(adminRole);
            admin.setEnabled(true);

            userRepository.save(admin);

            System.out.println("✅ Default Admin Created");
        }
    }
}