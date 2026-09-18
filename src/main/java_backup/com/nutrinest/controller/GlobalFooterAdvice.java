package com.nutrinest.controller;

import com.nutrinest.entity.User;
import com.nutrinest.service.NewsletterService;
import com.nutrinest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalFooterAdvice {

    @Autowired
    private NewsletterService newsletterService;

    @Autowired
    private UserService userService;


    @ModelAttribute
    public void addNewsletterStatus(
            Model model,
            Principal principal) {

        /*
         * Default:
         * Guest user / not subscribed
         */
        model.addAttribute(
                "newsletterSubscribed",
                false
        );


        /*
         * Logged-in user
         */
        if (principal != null) {

            User user =
                    userService.findByEmail(principal.getName());

            if (user != null) {

                boolean subscribed =
                        newsletterService.isSubscribed(
                                user.getEmail()
                        );

                model.addAttribute(
                        "newsletterSubscribed",
                        subscribed
                );
            }
        }
    }
}