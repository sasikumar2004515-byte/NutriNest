package com.nutrinest.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Double price;

    private Double mrp;

    @Column(nullable = false)
    private Integer stock;

    private String weight;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    private Boolean featured = false;

    private Boolean organic = false;

    private Boolean bestSeller = false;

    private Boolean premium = false;

    private Boolean gift = false;

    private Boolean active = true;


    // =========================
    // DEFAULT CONSTRUCTOR
    // =========================

    public Product() {
    }


    // =========================
    // FULL CONSTRUCTOR
    // =========================

    public Product(Long id,
                   String productName,
                   Category category,
                   Double price,
                   Double mrp,
                   Integer stock,
                   String weight,
                   String description,
                   String imageUrl,
                   Boolean featured,
                   Boolean organic,
                   Boolean bestSeller,
                   Boolean premium,
                   Boolean gift,
                   Boolean active) {

        this.id = id;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.mrp = mrp;
        this.stock = stock;
        this.weight = weight;
        this.description = description;
        this.imageUrl = imageUrl;
        this.featured = featured;
        this.organic = organic;
        this.bestSeller = bestSeller;
        this.premium = premium;
        this.gift = gift;
        this.active = active;
    }


    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }


    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }


    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Transient
    public String getDisplayImageUrl() {
        if (imageUrl == null || imageUrl.isBlank()) return "/images/OIP.jpeg";
        if (imageUrl.startsWith("/")) return imageUrl;
        if (imageUrl.startsWith("uploads/")) return "/" + imageUrl;
        return "/images/" + imageUrl;
    }


    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }


    public Boolean getOrganic() {
        return organic;
    }

    public void setOrganic(Boolean organic) {
        this.organic = organic;
    }


    public Boolean getBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(Boolean bestSeller) {
        this.bestSeller = bestSeller;
    }


    // =========================
    // PREMIUM
    // =========================

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }


    // =========================
    // GIFT
    // =========================

    public Boolean getGift() {
        return gift;
    }

    public void setGift(Boolean gift) {
        this.gift = gift;
    }


    // =========================
    // ACTIVE
    // =========================

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
