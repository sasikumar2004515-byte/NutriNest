package com.nutrinest.config;

import com.nutrinest.entity.User;
import com.nutrinest.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    public GlobalControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("user")
    public User loggedInUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElse(null);
    }

    /**
     * Handles invalid request/business validation errors.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(
            IllegalArgumentException ex,
            Model model) {

        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", getSafeMessage(ex));

        return "error";
    }

    /**
     * Handles expected runtime/business errors.
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(
            RuntimeException ex,
            Model model) {

        model.addAttribute("errorTitle", "Something went wrong");
        model.addAttribute("errorMessage", getSafeMessage(ex));

        return "error";
    }

    /**
     * Final fallback for unexpected application errors.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(
            Exception ex,
            Model model) {

        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute(
                "errorMessage",
                "Something went wrong while processing your request."
        );

        return "error";
    }

    /**
     * Returns a user-friendly message without exposing
     * internal exception details when no message exists.
     */
    private String getSafeMessage(Exception ex) {

        String message = ex.getMessage();

        if (message == null || message.trim().isEmpty()) {
            return "The request could not be completed.";
        }

        return message;
    }
}