package com.nutrinest.controller;

import com.nutrinest.entity.Cart;
import com.nutrinest.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    // =========================================================
    // ADD TO CART - PRODUCT ID PATH
    // =========================================================

    @PostMapping("/add/{productId}")
    public String addToCartByPath(
            @PathVariable Long productId,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        cartService.addToCart(
                productId,
                principal.getName()
        );

        String referer = request.getHeader("Referer");

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }

        return "redirect:/products";
    }


    // =========================================================
    // ADD TO CART - FORM / AJAX
    // =========================================================

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null) {
            return "LOGIN";
        }

        cartService.addToCart(
                productId,
                quantity,
                principal.getName()
        );

        /*
         * AJAX request:
         * return server-side cart item count.
         */
        if ("XMLHttpRequest".equals(
                request.getHeader("X-Requested-With"))) {

            int count =
                    cartService
                            .getAllCartItems(principal.getName())
                            .size();

            return "SUCCESS:" + count;
        }

        /*
         * Normal HTML form.
         * Redirect back to the page.
         */
        String referer = request.getHeader("Referer");

        if (referer != null && !referer.isBlank()) {
            return "REDIRECT:" + referer;
        }

        return "REDIRECT:/cart";
    }


    // =========================================================
    // VIEW CART
    // =========================================================

    @GetMapping
    public String viewCart(
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems =
                cartService.getAllCartItems(
                        principal.getName()
                );

        double subtotal =
                cartItems.stream()
                        .mapToDouble(item ->
                                item.getProduct().getPrice()
                                        * item.getQuantity()
                        )
                        .sum();

        double delivery =
                subtotal >= 999 ? 0 : 99;

        double grandTotal =
                subtotal + delivery;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("delivery", delivery);
        model.addAttribute("grandTotal", grandTotal);

        return "cart";
    }


    // =========================================================
    // INCREASE QUANTITY
    // =========================================================

    @PostMapping("/increase/{id}")
    public String increase(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        cartService.increaseQuantity(
                id,
                principal.getName()
        );

        return "redirect:/cart";
    }


    // =========================================================
    // DECREASE QUANTITY
    // =========================================================

    @PostMapping("/decrease/{id}")
    public String decrease(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        cartService.decreaseQuantity(
                id,
                principal.getName()
        );

        return "redirect:/cart";
    }


    // =========================================================
    // REMOVE ITEM
    // =========================================================

    @PostMapping("/remove/{id}")
    public String remove(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        cartService.removeFromCart(
                id,
                principal.getName()
        );

        return "redirect:/cart";
    }


    // =========================================================
    // CHANGE WEIGHT
    // =========================================================

    @PostMapping("/change-weight")
    @ResponseBody
    public String changeWeight(
            @RequestParam Long cartId,
            @RequestParam String weight,
            Principal principal) {

        if (principal == null) {
            return "LOGIN";
        }

        cartService.changeWeight(
                cartId,
                weight,
                principal.getName()
        );

        return "SUCCESS";
    }
}