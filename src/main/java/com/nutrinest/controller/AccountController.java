package com.nutrinest.controller;

import com.nutrinest.entity.User;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.FileUploadService;
import com.nutrinest.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class AccountController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    public AccountController(
            UserRepository userRepository,
            UserService userService,
            PasswordEncoder passwordEncoder,
            FileUploadService fileUploadService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
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
            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,
            Authentication authentication)
            throws IOException {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));


        // =================================================
        // BASIC PROFILE DETAILS
        // =================================================

        user.setFullName(
                formUser.getFullName()
        );

        user.setPhone(
                formUser.getPhone()
        );


        // =================================================
        // IMAGE UPDATE
        // =================================================

        String oldImagePath =
                user.getProfileImage();

        String newImagePath = null;


        if (image != null && !image.isEmpty()) {

            /*
             * Upload new image FIRST.
             *
             * Old image remains untouched until
             * database save succeeds.
             */
            newImagePath =
                    fileUploadService.uploadProfileImage(
                            image
                    );

            user.setProfileImage(
                    newImagePath
            );
        }


        // =================================================
        // SAVE USER
        // =================================================

        try {

            userRepository.save(user);

        } catch (RuntimeException ex) {

            /*
             * Database save failed.
             *
             * Remove newly uploaded image.
             */
            if (newImagePath != null) {

                fileUploadService.deleteProfileImage(
                        newImagePath
                );
            }

            throw ex;
        }


        // =================================================
        // DELETE OLD IMAGE
        // =================================================

        if (newImagePath != null
                && oldImagePath != null
                && !oldImagePath.isBlank()
                && !oldImagePath.equals(newImagePath)) {

            fileUploadService.deleteProfileImage(
                    oldImagePath
            );
        }


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


        // =================================================
        // KEEP USER DATA AVAILABLE
        // =================================================

        model.addAttribute(
                "user",
                user
        );


        // =================================================
        // CURRENT PASSWORD
        // =================================================

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            model.addAttribute(
                    "error",
                    "Current password is incorrect."
            );

            return "change-password";
        }


        // =================================================
        // NEW PASSWORD != CURRENT
        // =================================================

        if (passwordEncoder.matches(
                newPassword,
                user.getPassword())) {

            model.addAttribute(
                    "error",
                    "New password must be different from your current password."
            );

            return "change-password";
        }


        // =================================================
        // CONFIRM PASSWORD
        // =================================================

        if (!newPassword.equals(
                confirmPassword)) {

            model.addAttribute(
                    "error",
                    "New password and confirm password do not match."
            );

            return "change-password";
        }


        // =================================================
        // STRONG PASSWORD VALIDATION
        // =================================================

        boolean strongPassword =
                newPassword.length() >= 8
                        && newPassword.matches(
                        ".*[A-Z].*"
                )
                        && newPassword.matches(
                        ".*[a-z].*"
                )
                        && newPassword.matches(
                        ".*[0-9].*"
                )
                        && newPassword.matches(
                        ".*[^A-Za-z0-9].*"
                );


        if (!strongPassword) {

            model.addAttribute(
                    "error",
                    "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character."
            );

            return "change-password";
        }


        // =================================================
        // UPDATE PASSWORD
        // =================================================

        userService.updatePassword(
                authentication.getName(),
                newPassword
        );


        // =================================================
        // SUCCESS
        // =================================================

        model.addAttribute(
                "success",
                "Password changed successfully."
        );


        return "change-password";
    }
}