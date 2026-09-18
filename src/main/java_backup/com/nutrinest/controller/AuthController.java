package com.nutrinest.controller;

import com.nutrinest.entity.User;
import com.nutrinest.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    // =========================================================
    // REGISTER PAGE
    // =========================================================

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }


    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            Model model) {

        System.out.println("=================================");
        System.out.println("REGISTER REQUEST RECEIVED");
        System.out.println("Name  : " + fullName);
        System.out.println("Email : " + email);
        System.out.println("Phone : " + phone);
        System.out.println("=================================");

        try {

            // Create User object manually
            User user = new User();

            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);

            // Register
            userService.register(user);

            System.out.println("USER REGISTERED SUCCESSFULLY");

            model.addAttribute(
                    "success",
                    "Registration Successful! Please Login."
            );

            return "login";

        } catch (Exception e) {

            e.printStackTrace();

            String message = e.getMessage();

            System.out.println(
                    "ERROR MESSAGE = " + message
            );


            // Email error
            if (message != null &&
                    message.toLowerCase().contains("email")) {

                model.addAttribute(
                        "emailError",
                        message
                );
            }


            // Phone / Mobile error
            else if (message != null &&
                    (message.toLowerCase().contains("phone")
                            || message.toLowerCase().contains("mobile"))) {

                model.addAttribute(
                        "phoneError",
                        message
                );
            }


            // Other error
            else {

                model.addAttribute(
                        "error",
                        message != null
                                ? message
                                : "Registration failed. Please try again."
                );
            }


            // Preserve entered values
            User user = new User();

            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);

            model.addAttribute(
                    "user",
                    user
            );

            return "register";
        }
    }


    // =========================================================
    // FORGOT PASSWORD PAGE
    // =========================================================

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }


    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        User user = userService.findByEmail(email);

        if (user == null) {

            model.addAttribute(
                    "error",
                    "Email address not found."
            );

            return "forgot-password";
        }


        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            return "forgot-password";
        }


        String passwordPattern =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])"
                        + "[A-Za-z\\d@$!%*?&]{8,}$";


        if (!password.matches(passwordPattern)) {

            model.addAttribute(
                    "error",
                    "Password must contain at least 8 characters, "
                            + "one uppercase letter, one lowercase letter, "
                            + "one number, and one special character."
            );

            return "forgot-password";
        }


        userService.updatePassword(
                email,
                password
        );


        model.addAttribute(
                "success",
                "Password reset successfully. Please login."
        );

        return "login";
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "email",
                    email
            );

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            return "reset-password";
        }


        userService.updatePassword(
                email,
                password
        );


        model.addAttribute(
                "success",
                "Password reset successfully. Please login."
        );

        return "login";
    }
}