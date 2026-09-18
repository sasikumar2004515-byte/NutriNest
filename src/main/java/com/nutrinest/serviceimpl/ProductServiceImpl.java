package com.nutrinest.serviceimpl;

import com.nutrinest.entity.Category;
import com.nutrinest.entity.Product;
import com.nutrinest.repository.ProductRepository;
import com.nutrinest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;


    // =========================================================
    // SAVE PRODUCT
    // =========================================================

    @Override
    public Product saveProduct(Product product) {

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        return productRepository.save(product);
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================
    // Used mainly by ADMIN.
    // Includes both ACTIVE and INACTIVE products so that
    // admin can manage/reactivate inactive products.
    // =========================================================

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================
    // This method is kept for ADMIN / internal operations.
    // It can return inactive products because admin may need
    // to edit or manage them.
    // =========================================================

    @Override
    public Optional<Product> getProductById(Long id) {

        if (id == null) {
            return Optional.empty();
        }

        return productRepository.findById(id);
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Override
    public Product updateProduct(Long id, Product product) {

        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        Product oldProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product Not Found"));


        // =====================================================
        // BASIC PRODUCT DETAILS
        // =====================================================

        oldProduct.setProductName(product.getProductName());
        oldProduct.setCategory(product.getCategory());

        oldProduct.setPrice(product.getPrice());
        oldProduct.setMrp(product.getMrp());

        oldProduct.setStock(product.getStock());
        oldProduct.setWeight(product.getWeight());

        oldProduct.setDescription(product.getDescription());
        oldProduct.setImageUrl(product.getImageUrl());


        // =====================================================
        // PRODUCT FLAGS
        // =====================================================

        oldProduct.setFeatured(product.getFeatured());
        oldProduct.setOrganic(product.getOrganic());
        oldProduct.setBestSeller(product.getBestSeller());
        oldProduct.setPremium(product.getPremium());
        oldProduct.setGift(product.getGift());


        // =====================================================
        // ACTIVE STATUS
        // =====================================================

        oldProduct.setActive(product.getActive());


        return productRepository.save(oldProduct);
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================
    //
    // IMPORTANT:
    // This is a SOFT DELETE.
    //
    // We do NOT physically delete the product because
    // existing order_items may reference this product.
    //
    // active = false
    //
    // This preserves:
    // - Order history
    // - Order items
    // - Product reference
    // - Old product information
    // =========================================================

    @Override
    public void deleteProduct(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product Not Found"));

        // Soft delete
        product.setActive(false);

        productRepository.save(product);
    }


    // =========================================================
    // GET ACTIVE PRODUCTS
    // =========================================================
    //
    // Customer-facing product listing should use this method.
    // Inactive products will NOT be returned.
    // =========================================================

    @Override
    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }


    // =========================================================
    // GET BEST SELLER PRODUCTS
    // =========================================================

    @Override
    public List<Product> getBestSellerProducts() {
        return productRepository
                .findByBestSellerTrueAndActiveTrue();
    }


    // =========================================================
    // GET ORGANIC PRODUCTS
    // =========================================================

    @Override
    public List<Product> getOrganicProducts() {
        return productRepository
                .findByOrganicTrueAndActiveTrue();
    }


    // =========================================================
    // GET FEATURED PRODUCTS
    // =========================================================

    @Override
    public List<Product> getFeaturedProducts() {
        return productRepository
                .findByFeaturedTrueAndActiveTrue();
    }


    // =========================================================
    // GET PREMIUM PRODUCTS
    // =========================================================

    @Override
    public List<Product> getPremiumProducts() {
        return productRepository
                .findByPremiumTrueAndActiveTrue();
    }


    // =========================================================
    // GET GIFT PRODUCTS
    // =========================================================

    @Override
    public List<Product> getGiftProducts() {
        return productRepository
                .findByGiftTrueAndActiveTrue();
    }


    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // =========================================================

    @Override
    public List<Product> getProductsByCategory(Category category) {

        if (category == null) {
            return List.of();
        }

        return productRepository
                .findByCategoryAndActiveTrue(category);
    }


    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @Override
    public List<Product> searchProducts(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return getActiveProducts();
        }

        return productRepository
                .findByProductNameContainingIgnoreCaseAndActiveTrue(
                        keyword.trim()
                );
    }
}