package com.nutrinest.controller;

import com.nutrinest.service.NewsletterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping("/newsletter/subscribe")
    public String subscribe(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        boolean success = newsletterService.subscribe(email);

        if (success) {
            redirectAttributes.addFlashAttribute(
                    "newsletterSuccess",
                    "Subscribed successfully! Your 2% instant discount is now active."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "newsletterError",
                    "Please enter a valid email address."
            );
        }

        return "redirect:/";
    }
}