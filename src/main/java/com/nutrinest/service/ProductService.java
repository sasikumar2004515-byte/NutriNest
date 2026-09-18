package com.nutrinest.service;

import com.nutrinest.entity.Category;
import com.nutrinest.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product saveProduct(Product product);

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    List<Product> getActiveProducts();

    List<Product> getBestSellerProducts();

    List<Product> getOrganicProducts();

    List<Product> getFeaturedProducts();

    List<Product> getPremiumProducts();

    List<Product> getGiftProducts();

    List<Product> getProductsByCategory(Category category);

    // Search products
    List<Product> searchProducts(String keyword);
}
