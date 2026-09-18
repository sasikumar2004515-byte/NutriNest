package com.nutrinest.repository;

import com.nutrinest.entity.Category;
import com.nutrinest.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // =========================================================
    // ACTIVE PRODUCTS
    // =========================================================

    List<Product> findByActiveTrue();


    // =========================================================
    // BEST SELLERS
    // =========================================================

    List<Product> findByBestSellerTrueAndActiveTrue();


    // =========================================================
    // ORGANIC PRODUCTS
    // =========================================================

    List<Product> findByOrganicTrueAndActiveTrue();


    // =========================================================
    // FEATURED PRODUCTS
    // =========================================================

    List<Product> findByFeaturedTrueAndActiveTrue();


    // =========================================================
    // PREMIUM PRODUCTS
    // =========================================================

    List<Product> findByPremiumTrueAndActiveTrue();


    // =========================================================
    // GIFT PRODUCTS
    // =========================================================

    List<Product> findByGiftTrueAndActiveTrue();


    // =========================================================
    // CATEGORY PRODUCTS
    // =========================================================

    List<Product> findByCategoryAndActiveTrue(Category category);


    // =========================================================
    // SEARCH
    // =========================================================

    List<Product> findByProductNameContainingIgnoreCaseAndActiveTrue(
            String productName
    );
}