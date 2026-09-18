package com.nutrinest.service;

import com.nutrinest.entity.Cart;

import java.util.List;

public interface CartService {

    // =========================
    // Add to Cart
    // =========================

    // Existing compatibility methods
    Cart addToCart(Long productId, String email);

    Cart addToCart(Long productId, Integer quantity, String email);

    // Variant-aware Add to Cart
    Cart addToCart(Long productId, Long variantId, Integer quantity, String email);

    // =========================
    // View Cart
    // =========================

    List<Cart> getAllCartItems(String email);

    // =========================
    // Remove Item
    // =========================

    void removeFromCart(Long cartId, String email);

    // =========================
    // Increase Quantity
    // =========================

    void increaseQuantity(Long cartId, String email);

    // =========================
    // Decrease Quantity
    // =========================

    void decreaseQuantity(Long cartId, String email);

    // =========================
    // Change Variant
    // =========================

    void changeVariant(Long cartId, Long variantId, String email);

    // Existing compatibility method
    void changeWeight(Long cartId, String weight, String email);
}