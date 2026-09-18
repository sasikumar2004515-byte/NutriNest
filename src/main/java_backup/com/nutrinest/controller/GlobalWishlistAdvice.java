package com.nutrinest.controller;

import com.nutrinest.entity.User;
import com.nutrinest.service.UserService;
import com.nutrinest.service.WishlistService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalWishlistAdvice {

    private final WishlistService wishlistService;
    private final UserService userService;

    public GlobalWishlistAdvice(
            WishlistService wishlistService,
            UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    @ModelAttribute
    public void addWishlistData(
            Model model,
            Principal principal) {

        Set<Long> wishlistProductIds = Set.of();

        if (principal != null) {

            User user = userService.findByEmail(principal.getName());

            if (user != null) {
                wishlistProductIds =
                        wishlistService.getWishlist(user)
                                .stream()
                                .map(item -> item.getProduct().getId())
                                .collect(Collectors.toSet());
            }
        }

        model.addAttribute(
                "wishlistProductIds",
                wishlistProductIds
        );
    }
}