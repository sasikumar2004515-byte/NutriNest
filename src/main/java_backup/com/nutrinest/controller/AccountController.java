package com.nutrinest.controller;

import com.nutrinest.entity.User;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class AccountController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AccountController(
            UserRepository userRepository,
            UserService userService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    // =====================================================
    // ACCOUNT / DASHBOARD
    // =====================================================

    @GetMapping("/account")
    public String account(
            Model model,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        model.addAttribute("user", user);

        return "account";
    }


    // =====================================================
    // OLD ACCOUNT URL
    // =====================================================

    @GetMapping("/my-account")
    public String myAccount(
            Model model,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        model.addAttribute("user", user);

        return "account";
    }


    // =====================================================
    // EDIT PROFILE
    // =====================================================

    @GetMapping("/account/edit")
    public String editProfile(
            Model model,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        model.addAttribute("user", user);

        return "edit-profile";
    }


    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    @PostMapping("/account/update")
    public String updateProfile(
            @ModelAttribute User formUser,
            @RequestParam("image") MultipartFile image,
            Authentication authentication)
            throws IOException {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));


        user.setFullName(
                formUser.getFullName()
        );

        user.setPhone(
                formUser.getPhone()
        );


        if (!image.isEmpty()) {

            String uploadDir =
                    "uploads/profile/";

            File directory =
                    new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }


            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + image.getOriginalFilename();


            Path path =
                    Paths.get(
                            uploadDir + fileName
                    );


            Files.copy(
                    image.getInputStream(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );


            user.setProfileImage(
                    "/uploads/profile/"
                            + fileName
            );
        }


        userRepository.save(user);

        return "redirect:/account";
    }


    // =====================================================
    // CHANGE PASSWORD PAGE
    // =====================================================

    @GetMapping("/account/change-password")
    public String changePasswordPage(
            Model model,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        model.addAttribute("user", user);

        return "change-password";
    }


    // =====================================================
    // CHANGE PASSWORD
    // =====================================================

    @PostMapping("/account/change-password")
    public String changePassword(
            @RequestParam("currentPassword")
            String currentPassword,

            @RequestParam("newPassword")
            String newPassword,

            @RequestParam("confirmPassword")
            String confirmPassword,

            Authentication authentication,
            Model model) {


        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));


        // -------------------------------------------------
        // Keep user data available to the page
        // -------------------------------------------------

        model.addAttribute("user", user);


        // -------------------------------------------------
        // Check Current Password
        // -------------------------------------------------

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            model.addAttribute(
                    "error",
                    "Current password is incorrect."
            );

            return "change-password";
        }


        // -------------------------------------------------
        // Check New Password != Current Password
        // -------------------------------------------------

        if (passwordEncoder.matches(
                newPassword,
                user.getPassword())) {

            model.addAttribute(
                    "error",
                    "New password must be different from your current password."
            );

            return "change-password";
        }


        // -------------------------------------------------
        // Check Confirm Password
        // -------------------------------------------------

        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "New password and confirm password do not match."
            );

            return "change-password";
        }


        // -------------------------------------------------
        // Strong Password Validation
        // -------------------------------------------------

        boolean strongPassword =
                newPassword.length() >= 8
                        && newPassword.matches(".*[A-Z].*")
                        && newPassword.matches(".*[a-z].*")
                        && newPassword.matches(".*[0-9].*")
                        && newPassword.matches(".*[^A-Za-z0-9].*");


        if (!strongPassword) {

            model.addAttribute(
                    "error",
                    "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character."
            );

            return "change-password";
        }


        // -------------------------------------------------
        // Update Password
        // -------------------------------------------------

        userService.updatePassword(
                authentication.getName(),
                newPassword
        );


        // -------------------------------------------------
        // Success
        // -------------------------------------------------

        model.addAttribute(
                "success",
                "Password changed successfully."
        );


        return "change-password";
    }

}