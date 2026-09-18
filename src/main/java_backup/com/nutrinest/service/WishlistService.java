package com.nutrinest.service;

import java.util.List;

import com.nutrinest.entity.User;
import com.nutrinest.entity.Wishlist;

public interface WishlistService {

    void addToWishlist(
            Long productId,
            String email
    );

    void removeFromWishlist(
            Long productId,
            String email
    );

    List<Wishlist> getWishlist(
            User user
    );

    boolean isProductInWishlist(
            Long productId,
            String email
    );
}