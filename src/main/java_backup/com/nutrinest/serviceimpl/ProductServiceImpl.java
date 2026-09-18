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
        return productRepository.save(product);
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Override
    public Product updateProduct(Long id, Product product) {

        Optional<Product> existingProduct =
                productRepository.findById(id);

        if (existingProduct.isPresent()) {

            Product oldProduct = existingProduct.get();

            oldProduct.setProductName(product.getProductName());
            oldProduct.setCategory(product.getCategory());

            oldProduct.setPrice(product.getPrice());
            oldProduct.setMrp(product.getMrp());

            oldProduct.setStock(product.getStock());
            oldProduct.setWeight(product.getWeight());

            oldProduct.setDescription(product.getDescription());
            oldProduct.setImageUrl(product.getImageUrl());

            // Product flags
            oldProduct.setFeatured(product.getFeatured());
            oldProduct.setOrganic(product.getOrganic());
            oldProduct.setBestSeller(product.getBestSeller());

            // IMPORTANT:
            // Premium and Gift flags were missing before.
            // Without these, admin changes won't reflect
            // in Premium Mixed Nuts / Gift Collection.
            oldProduct.setPremium(product.getPremium());
            oldProduct.setGift(product.getGift());

            oldProduct.setActive(product.getActive());

            return productRepository.save(oldProduct);
        }

        return null;
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }


    // =========================================================
    // GET ACTIVE PRODUCTS
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
        return productRepository.findByBestSellerTrueAndActiveTrue();
    }


    // =========================================================
    // GET ORGANIC PRODUCTS
    // =========================================================

    @Override
    public List<Product> getOrganicProducts() {
        return productRepository.findByOrganicTrueAndActiveTrue();
    }


    // =========================================================
    // GET FEATURED PRODUCTS
    // =========================================================

    @Override
    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue();
    }


    // =========================================================
    // GET PREMIUM PRODUCTS
    // =========================================================

    @Override
    public List<Product> getPremiumProducts() {
        return productRepository.findByPremiumTrueAndActiveTrue();
    }


    // =========================================================
    // GET GIFT PRODUCTS
    // =========================================================

    @Override
    public List<Product> getGiftProducts() {
        return productRepository.findByGiftTrueAndActiveTrue();
    }


    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // =========================================================

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategoryAndActiveTrue(category);
    }


    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @Override
    public List<Product> searchProducts(String keyword) {

        return productRepository
                .findByProductNameContainingIgnoreCaseAndActiveTrue(keyword);
    }
}