package com.nutrinest.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Logged-in User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Selected Product Variant
    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;

    // Quantity
    @Column(nullable = false)
    private Integer quantity;

    // Legacy/backward-compatible selected weight
    @Column(name = "selected_weight")
    private String selectedWeight;

    public Cart() {
    }

    public Cart(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public Cart(User user, Product product, ProductVariant productVariant, Integer quantity) {
        this.user = user;
        this.product = product;
        this.productVariant = productVariant;
        this.quantity = quantity;

        if (productVariant != null) {
            this.selectedWeight = productVariant.getWeight();
        }
    }

    // ======================
    // Getters and Setters
    // ======================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;

        if (productVariant != null) {
            this.selectedWeight = productVariant.getWeight();
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSelectedWeight() {
        return selectedWeight;
    }

    public void setSelectedWeight(String selectedWeight) {
        this.selectedWeight = selectedWeight;
    }
}