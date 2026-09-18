package com.nutrinest.serviceimpl;

import com.nutrinest.entity.Role;
import com.nutrinest.entity.User;
import com.nutrinest.repository.RoleRepository;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =====================================================
    // REGISTER USER
    // =====================================================

    @Override
    public User register(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User information is required."
            );
        }


        // =================================================
        // EMAIL VALIDATION
        // =================================================

        String email = user.getEmail();

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        email = email.trim().toLowerCase();

        user.setEmail(email);


        // =================================================
        // PASSWORD VALIDATION
        // =================================================

        String password = user.getPassword();

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }


        // =================================================
        // PHONE VALIDATION
        // =================================================

        String phone = user.getPhone();

        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mobile number is required."
            );
        }

        phone = phone.trim();

        user.setPhone(phone);


        // =================================================
        // CHECK EMAIL
        // =================================================

        if (userRepository.findByEmail(email).isPresent()) {

            throw new RuntimeException(
                    "An account with this email already exists. Please login or use a different email."
            );
        }


        // =================================================
        // CHECK PHONE
        // =================================================

        if (userRepository.findByPhone(phone).isPresent()) {

            throw new RuntimeException(
                    "This mobile number is already registered. Please use another number."
            );
        }


        // =================================================
        // GET CUSTOMER ROLE
        // =================================================

        Role customerRole =
                roleRepository.findByRoleName("ROLE_CUSTOMER")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "ROLE_CUSTOMER not found"
                                )
                        );


        // =================================================
        // ENCODE PASSWORD
        // =================================================

        user.setPassword(
                passwordEncoder.encode(password)
        );


        // =================================================
        // DEFAULT VALUES
        // =================================================

        user.setRole(customerRole);

        user.setEnabled(true);


        // =================================================
        // SAVE USER
        // =================================================

        return userRepository.save(user);
    }


    // =====================================================
    // FIND USER BY EMAIL
    // =====================================================

    @Override
    public User findByEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElse(null);
    }


    // =====================================================
    // UPDATE PASSWORD
    // =====================================================

    @Override
    public void updatePassword(
            String email,
            String password) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }


        User user =
                userRepository
                        .findByEmail(
                                email.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // =================================================
        // ENCODE NEW PASSWORD
        // =================================================

        user.setPassword(
                passwordEncoder.encode(password)
        );


        // =================================================
        // SAVE USER
        // =================================================

        userRepository.save(user);
    }


    // =====================================================
    // GET ALL USERS
    // =====================================================

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
}