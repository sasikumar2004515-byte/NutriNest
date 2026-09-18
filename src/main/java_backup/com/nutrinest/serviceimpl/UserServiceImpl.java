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

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {

        // Check Email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException(
                    "An account with this email already exists. Please login or use a different email.");
        }

        // Check Phone
        System.out.println("Phone Entered : " + user.getPhone());

        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            System.out.println("PHONE EXISTS");
            throw new RuntimeException(
                    "This mobile number is already registered. Please use another number.");
        }

        // Get Customer Role
        Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));

        // Encode Password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set Default Values
        user.setRole(customerRole);
        user.setEnabled(true);

        // Save User
        return userRepository.save(user);
    }
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void updatePassword(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}