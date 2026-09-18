package com.nutrinest.config;

import com.nutrinest.entity.User;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.repository.CartRepository;
import com.nutrinest.repository.WishlistRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;

    public GlobalModelAttributes(UserRepository userRepository,
                                 CartRepository cartRepository,
                                 WishlistRepository wishlistRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @ModelAttribute
    public void addCounts(Model model, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {

            userRepository.findByEmail(authentication.getName()).ifPresent(user -> {

                model.addAttribute("cartCount",
                        cartRepository.findByUser(user).size());

                model.addAttribute("wishlistCount",
                        wishlistRepository.findByUser(user).size());

            });

        } else {

            model.addAttribute("cartCount", 0);
            model.addAttribute("wishlistCount", 0);
        }
    }

}