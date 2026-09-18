package com.nutrinest.controller;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nutrinest.entity.User;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.WishlistService;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;


    public WishlistController(
            WishlistService wishlistService,
            UserRepository userRepository) {

        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
    }


    // =========================================================
    // ADD TO WISHLIST
    // =========================================================

    @PostMapping("/add/{productId}")
    public String addToWishlist(
            @PathVariable Long productId,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        wishlistService.addToWishlist(
                productId,
                principal.getName()
        );

        String referer = request.getHeader("Referer");

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }

        return "redirect:/";
    }


    // =========================================================
    // REMOVE FROM WISHLIST
    // =========================================================

    @PostMapping("/remove/{productId}")
    public String removeFromWishlist(
            @PathVariable Long productId,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        wishlistService.removeFromWishlist(
                productId,
                principal.getName()
        );

        String referer = request.getHeader("Referer");

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }

        return "redirect:/wishlist";
    }


    // =========================================================
    // WISHLIST PAGE
    // =========================================================

    @GetMapping
    public String wishlist(
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        model.addAttribute(
                "wishlist",
                wishlistService.getWishlist(user)
        );

        return "wishlist";
    }
}