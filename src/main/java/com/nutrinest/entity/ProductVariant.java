package com.nutrinest.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // PRODUCT
    // =========================================================

    @ManyToOne
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;


    // =========================================================
    // WEIGHT
    // =========================================================

    @Column(
            nullable = false
    )
    private String weight;


    // =========================================================
    // SELLING PRICE
    // =========================================================

    @Column(
            nullable = false
    )
    private Double price;


    // =========================================================
    // MRP
    // =========================================================

    @Column
    private Double mrp;


    // =========================================================
    // STOCK
    // =========================================================

    @Column(
            nullable = false
    )
    private Integer stock;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public ProductVariant() {
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ProductVariant(
            Product product,
            String weight,
            Double price,
            Double mrp,
            Integer stock) {

        this.product = product;
        this.weight = weight;
        this.price = price;
        this.mrp = mrp;
        this.stock = stock;
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // SET ID
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // GET PRODUCT
    // =========================================================

    public Product getProduct() {
        return product;
    }


    // =========================================================
    // SET PRODUCT
    // =========================================================

    public void setProduct(Product product) {
        this.product = product;
    }


    // =========================================================
    // GET WEIGHT
    // =========================================================

    public String getWeight() {
        return weight;
    }


    // =========================================================
    // SET WEIGHT
    // =========================================================

    public void setWeight(String weight) {
        this.weight = weight;
    }


    // =========================================================
    // GET SELLING PRICE
    // =========================================================

    public Double getPrice() {
        return price;
    }


    // =========================================================
    // SET SELLING PRICE
    // =========================================================

    public void setPrice(Double price) {
        this.price = price;
    }


    // =========================================================
    // GET MRP
    // =========================================================

    public Double getMrp() {
        return mrp;
    }


    // =========================================================
    // SET MRP
    // =========================================================

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }


    // =========================================================
    // GET STOCK
    // =========================================================

    public Integer getStock() {
        return stock;
    }


    // =========================================================
    // SET STOCK
    // =========================================================

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}