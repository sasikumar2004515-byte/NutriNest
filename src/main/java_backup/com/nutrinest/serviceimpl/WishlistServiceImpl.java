package com.nutrinest.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutrinest.entity.Product;
import com.nutrinest.entity.User;
import com.nutrinest.entity.Wishlist;
import com.nutrinest.repository.ProductRepository;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.repository.WishlistRepository;
import com.nutrinest.service.WishlistService;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    // =========================================================
    // ADD TO WISHLIST
    // =========================================================

    @Override
    @Transactional
    public void addToWishlist(
            Long productId,
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product not found"
                                )
                        );

        if (wishlistRepository
                .findByUserAndProduct(user, product)
                .isEmpty()) {

            wishlistRepository.save(
                    new Wishlist(user, product)
            );
        }
    }


    // =========================================================
    // REMOVE FROM WISHLIST
    // =========================================================

    @Override
    @Transactional
    public void removeFromWishlist(
            Long productId,
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product not found"
                                )
                        );

        wishlistRepository.deleteByUserAndProduct(
                user,
                product
        );
    }


    // =========================================================
    // GET WISHLIST
    // =========================================================

    @Override
    public List<Wishlist> getWishlist(User user) {

        return wishlistRepository.findByUser(user);
    }


    // =========================================================
    // CHECK PRODUCT IN WISHLIST
    // =========================================================

    @Override
    public boolean isProductInWishlist(
            Long productId,
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product not found"
                                )
                        );

        return wishlistRepository
                .findByUserAndProduct(user, product)
                .isPresent();
    }
}