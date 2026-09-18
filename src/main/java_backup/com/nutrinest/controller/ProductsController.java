package com.nutrinest.controller;

import com.nutrinest.entity.Product;
import com.nutrinest.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductsController {

    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }


    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Product> products;

        if (keyword == null || keyword.trim().isEmpty()) {

            products = productService.getActiveProducts();

        } else {

            products = productService.searchProducts(keyword.trim());

        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);

        return "search";
    }
}