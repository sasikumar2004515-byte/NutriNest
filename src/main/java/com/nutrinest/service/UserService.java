package com.nutrinest.service;

import com.nutrinest.entity.User;

import java.util.List;

public interface UserService {

    User register(User user);

    User findByEmail(String email);

    void updatePassword(String email, String password);

    List<User> getAllUsers();
}